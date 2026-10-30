public class CommercialJet extends Aircraft {
    public CommercialJet(String cs, double speed, double dist) { super(cs, speed, WakeCategory.MEDIUM, dist); }
    @Override public int getPriority() { return 2; }
    @Override protected String describe() { return "Commercial Jet"; }
}
