/** Contract every aircraft under ATC control must satisfy. */
public interface ControllableAircraft {
    String getCallsign();
    double getApproachSpeed();            // knots
    WakeCategory getWakeCategory();
    int getPriority();                    // lower value = higher priority
    double getDistanceToThreshold();      // nautical miles
    void updatePosition(double deltaSeconds);
    void setDistanceToThreshold(double nm);
    void executeCommand(String command);  // e.g. "GO_AROUND"
}
