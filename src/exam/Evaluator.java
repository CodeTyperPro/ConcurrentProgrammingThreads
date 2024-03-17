import java.util.*;
import java.util.concurrent.*;

public class Evaluator {
	// Maximum number of players under evaluation
	private static final int LIMIT = 2;
	private static final int WAITING_TIME_MSEC = 1000;
	private static final int EVALUATION_PREPARTION_TIME_MSEC = 500;
	
	private static Evaluator instance;
	
	private Evaluator() {}
	
	/**
	  * TODO Part 3: Create a collection for players
	  * TODO Part 3: Create a variable keeping track whether new players can be evaluated or not
	  */
	
	// List<Player> registeredPlayers = Collections.synchronizedList(new ArrayList<>(LIMIT));
	BlockingQueue<Player> queues;
	final Map<Player, Boolean> trackPlayer = Collections.synchronizedMap(new HashMap<>());
	
	AtomicBoolean isReady = new AtomicBoolean(false);

	public static Evaluator getInstance() {
		if (instance == null) {
			instance = new Evaluator();
		}
		return instance;
	}
	
	public void startEvaluation() {
		queues = new LinkedBlockingQueue<>();

		while (true) {
			// Part 3
			// TODO print out how many players are to be evaluated
			int howMany = LIMIT - registeredPlayers.size();
			System.out.println(howMany + " are set to be evaluated!");

			// TODO use the previously created variable to indicate the Evaluator is ready to evaluate players
			isReady.set(true);

			// TODO Wait for WAITING_TIME_MSEC
			Thread.Sleep(WAIT_TIME_MSEC);

			// TODO set the previously created variable to false
			isReady.set(false);

			// TODO Wait for EVALUATION_PREPARTION_TIME_MSEC
			Thread.sleep(EVALUATION_PREPARTION_TIME_MSEC);

			// TODO If there is no players to evaluate in the data structure then print this information and exit the loop
			if (queues.isEmpty()) break;

			// TODO Otherwise generate a random number to calcute the win/loss of the player and print it
			long rand = ThreadLocalRandom.current().nextInt(-10, 10); // ???
			if (rand == 0) {
				rand++;
			}

			Player player = queues.poll();
			int prize = takeInput.getParticipationCost() * rand;
			if (rand > 0) {
				System.out.println("Prize = " +  prize);
			} else {
				System.out.println("Loss = " +  prize);
			}

			trackPlayer.put(player, true);
		}	
	}
	
	public boolean tryEvaluatePlayer(Player player) {
		// TODO 
		if (trackPlayer.contains(Player)) {
			System.out.println("Can not be evaluated!");
			return false;
		}

		if (queues.size() == LIMIT) {
			System.out.println("Full data structure!");
			return false;
		}
		
		System.out.println("New evaluations are possible!");
        queues.put(player);

		return true;
	}
}


