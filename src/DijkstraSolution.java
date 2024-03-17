import java.util.Random;
import java.util.concurrent.Semaphore;

public class DijkstraSolution {
    static final int N = 5; // number of philosophers (and forks)
    enum State {
        THINKING, // philosopher is THINKING
        HUNGRY,   // philosopher is trying to get forks
        EATING    // philosopher is EATING
    }

    static State[] state = new State[N]; // array to keep track of everyone's state
    static Semaphore[] bothForksAvailable = new Semaphore[N];

    static int left(int i) {
        return (i - 1 + N) % N;
    }

    static int right(int i) {
        return (i + 1) % N;
    }

    static Random random = new Random();

    static int myRand(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    static void test(int i) {
        if (state[i] == State.HUNGRY &&
                state[left(i)] != State.EATING &&
                state[right(i)] != State.EATING) {
            state[i] = State.EATING;
            bothForksAvailable[i].release(); // forks are no longer needed for this eat session
        }
    }

    static void think(int i) {
        int duration = myRand(400, 800);
        System.out.println(i + " is thinking for " + duration + "ms");
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void takeForks(int i) {
        state[i] = State.HUNGRY;
        System.out.println("\t\t" + i + " is HUNGRY");
        test(i);
        if (state[i] != State.EATING) {
            try {
                bothForksAvailable[i].acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static void eat(int i) {
        int duration = myRand(400, 800);
        System.out.println("\t\t\t\t" + i + " is eating for " + duration + "ms");
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void putForks(int i) {
        state[i] = State.THINKING;
        test(left(i));
        test(right(i));
    }

    static void philosopher(int i) {
        while (true) {
            think(i);
            takeForks(i);
            eat(i);
            putForks(i);
        }
    }

    public static void main(String[] args) {
        System.out.println("Dining Philosophers");

        for (int i = 0; i < N; i++) {
            bothForksAvailable[i] = new Semaphore(0);
            final int index = i;
            new Thread(() -> philosopher(index)).start();
        }
    }
}