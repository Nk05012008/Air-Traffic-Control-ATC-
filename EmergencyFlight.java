public class EmergencyFlight extends Aircraft {
    public EmergencyFlight(String cs, double speed, double dist) { super(cs, speed, WakeCategory.MEDIUM, dist); }
    @Override public int getPriority() { return 1; }
    @Override protected String describe() { return "Emergency Flight"; }
}
