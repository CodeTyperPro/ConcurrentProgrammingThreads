package done.assignment;

import java.util.stream.IntStream;

public class DataMover {

    // Class variables
    public static Thread[] movers;
    public static int[] data;
    private static Object[] locks;

    public static int SIZE;
    public static final int VALUE = 1000;

    public static final String[] DEFAULT_ARGS = {"123", "111", "256", "404"};

    public static void main(String[] args) {
        // Check for command-line arguments
        int len = args.length;
        if (len < 2) {
            len = DEFAULT_ARGS.length;
            args = DEFAULT_ARGS;

            System.out.println("Command-line arguments were not passed. DEFAULT values set instead ...");
        }

        // Parse simulation time
        long simulateTime = Integer.parseInt(args[0]);

        // Set array size and initialize arrays
        SIZE = len - 1;
        long[] waitTimeThread = new long[SIZE];

        movers = new Thread[SIZE];
        data = new int[SIZE];
        locks = new Object[SIZE];

        // Initialize array values and locks
        String[] finalArgs = args;
        IntStream.range(0, SIZE).forEach(i -> {
            data[i] = i * VALUE;
            locks[i] = new Object();
            waitTimeThread[i] = Long.parseLong(finalArgs[i]);
        });

        // Start threads
        for (int i = 0; i < SIZE; i++) {
            final int finalI = i;
            movers[i] = new Thread(() -> {
                try {
                    Thread.sleep(waitTimeThread[finalI]);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }

                IntStream.range(0, 10).forEach(_j -> {
                    synchronized (locks[finalI]) {
                        data[finalI] -= finalI;
                        System.out.println(String.format("#%d: data %d == %s", finalI, finalI, data[finalI]));
                    }

                    try {
                        Thread.sleep(simulateTime);
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }

                    int nextIndex = (finalI + 1) % SIZE;
                    synchronized (locks[nextIndex]) {
                        data[nextIndex] += finalI;
                        System.out.println(String.format("#%d: data %d -> %s", finalI, nextIndex, data[nextIndex]));
                    }
                });
            });
        }

        // Start threads
        IntStream.range(0, SIZE).forEach(_j -> movers[_j].start());

        // Join threads
        try {
            for (int i = 0; i < SIZE; i++) {
                movers[i].join();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Print output
        StringBuilder sb = new StringBuilder();
        sb.append("\nOUTPUT: \n");

        IntStream.range(0, SIZE).forEach(_j -> sb.append("[" + data[_j] + "] "));
        sb.append("\n");

        System.out.println(sb);
    }
}