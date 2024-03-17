import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.*;
import java.util.concurrent.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TournamentSimulator {
	
	// Time allocated to tournaments
	private static final int TOURNAMENT_DURATION_MSEC = 6000;
	// Time allocated to evaluation
	private static final int EVALUATION_DURATION_MSEC = 8000;

	private static final AtomicInteger STATUS_SIMULATION = new AtomicInteger(0);
	
	// List of tournaments
	private static final List<Tournament> tournaments = List.of(
		new Tournament("NLH", 2500, 1000, 10),
		new Tournament("PLO", 2000, 1000, 20),
		new Tournament("Seven Card Stud", 1500, 1000, 30),
		new Tournament("PLO-5", 2000, 1000, 40),
		new Tournament("PLH", 1500, 100, 50),
		new Tournament("Razz", 2500, 1000, 60)
	);

	private static final List<Player> players = List.of(
		new Player("Gyuri bacsi"),
		new Player("Orsi"),
		new Player("Robi"),
		new Player("Vin"),
		new Player("Emric"),
		new Player("Evelin")
	);
	
	/**
	 * Part 1:
	 * 1. Invoke setup() method
	 * 2. Invoke the players' waitForTournament() method on a separate to start waiting for their tournaments
	 * 3. Invoke the tournaments' start() method on a separate thread
	 * 4. Wait for TOURNAMENT_DURATION_MSEC
	 * Part 2:
	 * After TOURNAMENT_DURATION_MSEC let the players know the tournament is over
	 * Part 3:
	 * Start the evaluation by running startEvaluation on a separate thread
	 * Finally terminate the program after at most EVALUATION_DURATION_MSEC time
	 */
	public static void main(String[] args) {
		setup();

        ExecutorService es = Executors.newCachedThreadPool();

		// PLAYER
		es.submit(() -> {
			Player p = new Player("A");
			p.waitForTournament();
			
			final long startTime = System.nanoTime();
			final long endTime = System.currentTimeMillis() + TOURNAMENT_DURATION_MSEC;
			 while (true) {
                final long now = System.currentTimeMillis();
                if (now >= endTime) {
					System
					break;
				};
			 }

			 p.evaluate();
		});

		Evaluator e = Evaluator.getInstance();
		while (!e.tryEvaluatePlayer()) {
			Thread.sleep(WAIT_TIME_MSEC);
		}

		// TOURNAMENT
		es.submit(() -> {
			final long startTime = System.nanoTime();
			Tournament t = new Tournament(startTime, TOURNAMENT_DURATION_MSEC, 0);
			t.start();

			final long endTime = System.currentTimeMillis() + TOURNAMENT_DURATION_MSEC;
			 while (true) {
                final long now = System.currentTimeMillis();
                if (now >= endTime) break;
			 }

			 STATUS_SIMULATION.set(3); // finished
		});
		
		es.shutdown();
        try {
            es.awaitTermination(EVALUATION_DURATION_MSEC, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {}
	}
	
	private static void setup() {
		for (int i = 0; i < tournaments.size(); ++i) {
			tournaments.get(i).addPlayer(players.get(i));
		}
	}
}
