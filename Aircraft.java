/** Abstract base class: holds encapsulated state shared by all aircraft. */
public abstract class Aircraft implements ControllableAircraft {
    private final String callsign;
    private final double approachSpeed;
    private final WakeCategory wakeCategory;
    private double distanceToThreshold;
    private volatile boolean goingAround = false;

    protected Aircraft(String callsign, double approachSpeed,
                       WakeCategory wake, double distanceNm) {
        if (callsign == null || callsign.isBlank())
            throw new IllegalArgumentException("Callsign required");
        if (approachSpeed <= 0)
            throw new IllegalArgumentException("Speed must be positive");
        this.callsign = callsign;
        this.approachSpeed = approachSpeed;
        this.wakeCategory = wake;
        this.distanceToThreshold = distanceNm;
    }
    @Override public String getCallsign() { return callsign; }
    @Override public double getApproachSpeed() { return approachSpeed; }
    @Override public WakeCategory getWakeCategory() { return wakeCategory; }
    @Override public synchronized double getDistanceToThreshold() { return distanceToThreshold; }
    @Override public synchronized void setDistanceToThreshold(double nm) { distanceToThreshold = nm; }
    public boolean isGoingAround() { return goingAround; }

    /** Distance reduces by speed(kt) * dt / 3600 nautical miles. */
    @Override public synchronized void updatePosition(double dt) {
        if (!goingAround) distanceToThreshold = Math.max(0, distanceToThreshold - approachSpeed * dt / 3600.0);
    }
    @Override public void executeCommand(String command) {
        if ("GO_AROUND".equals(command)) goingAround = true;
        System.out.printf("   [%s] %s acknowledges %s%n", callsign, describe(), command);
    }
    protected abstract String describe();
}
