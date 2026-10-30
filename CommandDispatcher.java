import java.util.concurrent.PriorityBlockingQueue;

/** Consumer thread: executes queued commands highest-priority first. */
public class CommandDispatcher extends Thread {
    private final PriorityBlockingQueue<GoAroundCommand> queue;
    private final IncidentLogger logger;
    private volatile boolean running = true;
    public CommandDispatcher(PriorityBlockingQueue<GoAroundCommand> q, IncidentLogger l) { super("CommandDispatcher"); queue = q; logger = l; }
    public void shutdown() { running = false; interrupt(); }
    @Override public void run() {
        while (running) {
            try {
                GoAroundCommand c = queue.take();
                logger.log("CMD", "GO-AROUND advisory broadcast to " + c.getTarget().getCallsign() + " (" + c.getReason() + ")");
                c.execute();
                logger.log("INCIDENT", "Safety incident recorded for " + c.getTarget().getCallsign() + " at " + c.getTimestamp());
            } catch (InterruptedException e) { running = false; }
        }
    }
}
