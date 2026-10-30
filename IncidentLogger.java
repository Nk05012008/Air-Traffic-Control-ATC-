import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Thread-safe safety incident log with millisecond timestamps. */
public class IncidentLogger {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private final List<String> entries = Collections.synchronizedList(new ArrayList<>());
    public void log(String severity, String message) {
        String line = "[" + LocalDateTime.now().format(FMT) + "] [" + severity + "] " + message;
        entries.add(line);
        System.out.println(line);
    }
    public List<String> getEntries() { return new ArrayList<>(entries); }
    public int size() { return entries.size(); }
}
