/**
  * Represents a player of the poker tournament.
  * They wait for the tournament to start then
  * attend at it. Finally, their results will be
  * evaluated.
  */

import java.util.*;
import java.util.concurrent.*;

public class Player {
	private static final int WAIT_TIME_MSEC = 1000;
	private final String name;
	private int money;
	private int participationCost = 0;
	
	public Player(String name) {
		this.name = name;
		money = 1000;
	}

	public synchronized void waitForTournament() {
		// TODO Part 1.: Wait until the tournament starts.
        while (TournamentSimulator.STATUS_SIMULATION == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                STATUS_SIMULATION = 1;
            }
        }
		
		// TODO Part 1.: After the tournament started invoke participateInTournament()
		while (STATUS_SIMULATION == 1) {
			participateInTournament();
			break;
		}
	}

	public void participateInTournament() {
		// TODO Part 1.: Wait until the tournament finishes
		while (STATUS_SIMULATION != 2) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
	}
	
	public void evaluate() {

	}
	
	public String getName() {
		return name;
	}
	
	public int getMoney() {
		return money;
	}
	
	public void addParticipationCost(int cost) {
		participationCost += cost;
	}
	
	public int getParticipationCost() {
		return participationCost;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
