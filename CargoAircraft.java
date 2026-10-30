public class CargoAircraft extends Aircraft {
    public CargoAircraft(String cs, double speed, double dist) { super(cs, speed, WakeCategory.HEAVY, dist); }
    @Override public int getPriority() { return 3; }
    @Override protected String describe() { return "Cargo Aircraft"; }
}
