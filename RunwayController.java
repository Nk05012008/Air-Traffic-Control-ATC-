import java.util.concurrent.locks.ReentrantLock;

/** Coordinates runway occupancy through an atomic lock. */
public class RunwayController {
    public static final double MIN_SEPARATION_NM = 1.2;
    public static final double MIN_SEPARATION_SEC = 60.0;

    private final String runwayId;
    private final ReentrantLock lock = new ReentrantLock();
    private ControllableAircraft clearedForLanding;
    private ControllableAircraft occupant;
    private final IncidentLogger logger;

    public RunwayController(String runwayId, IncidentLogger logger) { this.runwayId = runwayId; this.logger = logger; }
    public String getRunwayId() { return runwayId; }

    public void clearForLanding(ControllableAircraft a) {
        if (a == null) throw new InvalidClearanceException("No aircraft specified");
        lock.lock();
        try {
            if (clearedForLanding != null)
                throw new InvalidClearanceException("Runway " + runwayId + " already assigned to " + clearedForLanding.getCallsign());
            clearedForLanding = a;
            logger.log("INFO", a.getCallsign() + " cleared to land on Runway " + runwayId);
        } finally { lock.unlock(); }
    }

    public void enterRunway(ControllableAircraft a) {
        lock.lock();
        try {
            occupant = a;
            logger.log("WARN", a.getCallsign() + " entered Runway " + runwayId + " threshold");
        } finally { lock.unlock(); }
    }

    /** Deterministic conflict check: distance below minimum OR time-to-threshold under 60 s. */
    public void checkSeparation() throws RunwayIncursionException {
        lock.lock();
        try {
            if (clearedForLanding == null || occupant == null) return;
            double dist = clearedForLanding.getDistanceToThreshold();
            double timeSec = dist / clearedForLanding.getApproachSpeed() * 3600.0;
            if (dist < MIN_SEPARATION_NM || timeSec < MIN_SEPARATION_SEC)
                throw new RunwayIncursionException(occupant.getCallsign(), clearedForLanding.getCallsign(), dist);
        } finally { lock.unlock(); }
    }

    public ControllableAircraft cancelClearance() {
        lock.lock();
        try { ControllableAircraft a = clearedForLanding; clearedForLanding = null; return a; }
        finally { lock.unlock(); }
    }
    public void vacate() { lock.lock(); try { occupant = null; } finally { lock.unlock(); } }
    public boolean isOccupied() { lock.lock(); try { return occupant != null; } finally { lock.unlock(); } }
}
