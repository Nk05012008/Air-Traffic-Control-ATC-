import java.util.concurrent.BlockingQueue;

/** Radar sentinel: polls positions, computes closure rate, raises incursion alerts. */
public class ApproachRadarThread extends Thread {
    private final RunwayController controller;
    private final ControllableAircraft approaching;
    private final BlockingQueue<GoAroundCommand> commandQueue;
    private final IncidentLogger logger;
    private final long sweepMillis;
    private volatile boolean running = true;

    public ApproachRadarThread(RunwayController c, ControllableAircraft a,
                               BlockingQueue<GoAroundCommand> q, IncidentLogger l, long sweepMillis) {
        super("RadarSentinel");
        this.controller = c; this.approaching = a; this.commandQueue = q; this.logger = l; this.sweepMillis = sweepMillis;
    }
    public void shutdown() { running = false; interrupt(); }

    @Override public void run() {
        double previous = approaching.getDistanceToThreshold();
        int sweep = 0;
        while (running) {
            try {
                Thread.sleep(sweepMillis);
                approaching.updatePosition(15.0);               // 15 simulated seconds per sweep
                double now = approaching.getDistanceToThreshold();
                double closureKt = (previous - now) / (15.0 / 3600.0);   // vector closure rate
                previous = now;
                sweep++;
                logger.log("RADAR", String.format("Sweep #%d: %s dist=%.2f NM, closure=%.0f kt",
                        sweep, approaching.getCallsign(), now, closureKt));
                controller.checkSeparation();
            } catch (RunwayIncursionException e) {
                logger.log("ALERT", e.getMessage());
                GoAroundCommand cmd = new GoAroundCommand(approaching, "Runway occupied");
                controller.cancelClearance();
                logger.log("ALERT", "Landing clearance CANCELLED for " + approaching.getCallsign());
                commandQueue.offer(cmd);
                running = false;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }
}
