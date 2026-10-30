import java.util.concurrent.PriorityBlockingQueue;

public class ATCSimulation {
    public static void main(String[] args) throws Exception {
        IncidentLogger logger = new IncidentLogger();
        RunwayController runway = new RunwayController("09L", logger);
        PriorityBlockingQueue<GoAroundCommand> queue = new PriorityBlockingQueue<>();

        System.out.println("=== ATC Runway Conflict Detection & Separation Sentinel ===");
        System.out.println("--- Scenario 1: Normal operation (no conflict) ---");
        ControllableAircraft safe = new CommercialJet("UK-811", 140, 6.0);
        runway.clearForLanding(safe);
        for (int i = 0; i < 3; i++) { safe.updatePosition(15); runway.checkSeparation(); }
        System.out.println("   Separation maintained. No action required.");
        runway.cancelClearance();

        System.out.println("\n--- Scenario 2: Runway incursion ---");
        ControllableAircraft ai504 = new CommercialJet("AI-504", 140, 4.0);
        ControllableAircraft indigo = new CommercialJet("6E-201", 30, 0.0);
        CommandDispatcher dispatcher = new CommandDispatcher(queue, logger);
        dispatcher.start();
        runway.clearForLanding(ai504);
        ApproachRadarThread radar = new ApproachRadarThread(runway, ai504, queue, logger, 400);
        radar.start();
        Thread.sleep(900);
        runway.enterRunway(indigo);               // premature threshold entry
        radar.join();
        Thread.sleep(300);

        System.out.println("\n--- Scenario 3: Invalid clearance handling ---");
        try { runway.clearForLanding(new CommercialJet("SG-402", 135, 8)); runway.clearForLanding(new CargoAircraft("QR-901", 150, 9)); }
        catch (InvalidClearanceException e) { logger.log("ERROR", e.getMessage()); }

        System.out.println("\n--- Scenario 4: Priority queue (Emergency first) ---");
        dispatcher.shutdown();
        dispatcher.join();
        queue.offer(new GoAroundCommand(new CargoAircraft("QR-901", 150, 1.0), "Wake spacing"));
        queue.offer(new GoAroundCommand(new EmergencyFlight("MED-007", 130, 1.0), "Medical priority"));
        CommandDispatcher dispatcher2 = new CommandDispatcher(queue, logger);
        dispatcher2.start();
        Thread.sleep(400);
        dispatcher2.shutdown();
        System.out.println("\nTotal safety log entries: " + logger.size());
    }
}
