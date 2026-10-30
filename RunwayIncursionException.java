/** Checked exception raised when separation minima are violated. */
public class RunwayIncursionException extends Exception {
    private final String leader, trailer; private final double separationNm;
    public RunwayIncursionException(String leader, String trailer, double sepNm) {
        super(String.format("Runway incursion: %s too close to %s (%.2f NM)", trailer, leader, sepNm));
        this.leader = leader; this.trailer = trailer; this.separationNm = sepNm;
    }
    public String getLeader() { return leader; }
    public String getTrailer() { return trailer; }
    public double getSeparationNm() { return separationNm; }
}
