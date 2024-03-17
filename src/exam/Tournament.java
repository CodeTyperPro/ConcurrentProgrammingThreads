import java.util.*;
import java.util.concurrent.*;

public class Tournament {
	// TODO Create a collection for the player who would participate in the tournament
	List<Player> players =  Collections.synchronizedList(new ArrayList<>());
	
	private final int startTime;
	private final int duration;
	private final int cost;
	private final String name;

	public Tournament(String name, int startTime, int duration, int cost) {
		this.startTime = startTime;
		this.duration = duration;
		this.name = name;
		this.cost = cost;
	}

	public void start() {
		// TODO wait for startTime
		Thread.sleep(startTime);

		// TODO notify the players the tourname is about to start
		synchronized(this) {
			notifyAll();
		}

		// TODO handle the participation fee (cost)
		AtomicInteger cost = new AtomicInteger(0);
		synchronized(players) {
			for (int i = 0; i < players.length; i++) {
				players.get(i).addParticipationCost(cost.get());
			}

			cost.incrementAndGet(cost);
		}

		// TODO wait for duration
		Thread.sleep(duration);

		// TODO notify the players the tournament is over
		synchronized (this) {
            notifyAll();
        }
	}
	
	public void addPlayer(Player player) {
		players.add(players);
	}
}