import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** High-priority command with precise timestamp. */
public class GoAroundCommand implements Comparable<GoAroundCommand> {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private final ControllableAircraft target;
    private final String reason;
    private final LocalDateTime issuedAt = LocalDateTime.now();
    public GoAroundCommand(ControllableAircraft target, String reason) { this.target = target; this.reason = reason; }
    public ControllableAircraft getTarget() { return target; }
    public String getReason() { return reason; }
    public String getTimestamp() { return issuedAt.format(FMT); }
    public void execute() { target.executeCommand("GO_AROUND"); }
    @Override public int compareTo(GoAroundCommand o) { return Integer.compare(target.getPriority(), o.target.getPriority()); }
}
