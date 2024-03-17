package done.assignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class BackupDataMover2 {

    // Class variables
    public static AtomicInteger arrivalCount, totalSent, totalArrived;
    public static AtomicInteger totalArrivalCount;
    public static ExecutorService pool;
    public static List<BlockingDeque<Integer>> queues;

    public static List<Future<DataMover2Result>> moverResults;

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

        // Initialization of atomic integers
        arrivalCount = new AtomicInteger(0);
        totalSent = new AtomicInteger(0);
        totalArrived = new AtomicInteger(0);

        moverResults = Collections.synchronizedList(new ArrayList<Future<DataMover2Result>>());

        // Parse simulation time
        long simulateTime = Integer.parseInt(args[0]);

        // Set array size and initialize arrays
        SIZE = len - 1;
        totalArrivalCount = new AtomicInteger(2 * SIZE);

        long[] waitTimeThread = new long[SIZE];

        // Fill
        String[] finalArgs = args;
        queues = Collections.synchronizedList(new ArrayList<>());
        IntStream.range(0, SIZE).forEach(i -> {
            queues.add(new LinkedBlockingDeque<>());

            // Lets try to make all the threads busy
            IntStream.range(0, SIZE).forEach(j -> {
                Integer elem = ThreadLocalRandom.current().nextInt(0, 10_000);
                queues.get(i).add(elem);
            });

            waitTimeThread[i] = Long.parseLong(finalArgs[i]);
        });

        final int MAX_CAPACITY = 100;
        pool = Executors.newFixedThreadPool(MAX_CAPACITY);

        // Start tasks
        for (int i = 0; i < SIZE; i++) {
            int finalI = i;

            Callable<DataMover2Result> result = () -> {
                final DataMover2Result res = new DataMover2Result();

                int x = 0;
                final int n = SIZE;
                final int queueSize = queues.size();
                final int next = (finalI + 1) % queueSize;

                do {
                    String outputSuffix = "";
                    // sends x
                    x = ThreadLocalRandom.current().nextInt(0, 10_000);
                    try {
                        queues.get(finalI).put(x); // Blocking insert is 'put', not 'add' :) I knew!
                        totalSent.addAndGet(x);
                        outputSuffix = String.format("sends %d", x);

                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    String outputPrefix = String.format("total\t%d/%d\t|\t#%d ", arrivalCount.get(), totalArrivalCount.get(), finalI);
                    System.out.println(outputPrefix + outputSuffix);

                    long timeOut = ThreadLocalRandom.current().nextInt(300, 1000);

                    long now = 0;
                    long endTime = System.currentTimeMillis() + timeOut;
                    while (now < endTime) {

                        if (queues.get(next).isEmpty()) {
                            //secondOutput = "got nothing ...";
                        } else {
                            int takeInput = queues.get(next).take();
                            // System.out.println("xTake -> " + takeInput + " | next -> " + next);

                            if (takeInput % n == finalI) {
                                arrivalCount.addAndGet(1);
                                res.setData(takeInput);

                                outputSuffix = String.format("got %d", takeInput);
                                // System.out.println("ArrivalCount -> " + arrivalCount.get());
                                //System.exit(0);
                            } else {
                                queues.get(finalI).put(takeInput - 1);
                                res.incrementCount();

                                outputSuffix = String.format("forwards %d [%d]", takeInput, (takeInput + n) % n);
                            }

                            try {
                                Thread.sleep(waitTimeThread[finalI]);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        outputPrefix = String.format("total\t%d/%d\t|\t#%d ", arrivalCount.get(), totalArrivalCount.get(), finalI);
                        String output = outputPrefix + outputSuffix;
                        System.out.println(output);

                        now = System.currentTimeMillis();
                    }
                } while (x < 5 * n); // -> n: number of threads

                totalArrived.addAndGet(res.getData() + res.getForwarded()); // ???
                return res;
            };

            Future<DataMover2Result> future = pool.submit(result);
            moverResults.add(future); //future.get();

            try {
                Thread.sleep(simulateTime);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }

        pool.shutdown();

        try {
            pool.awaitTermination(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        List<Integer> discards = Collections.synchronizedList(new ArrayList<>());
        queues.forEach(queue -> queue.drainTo(discards));

        // Print output
        StringBuilder sb = new StringBuilder();
        AtomicInteger totalSum = new AtomicInteger(0);
        AtomicInteger totalDiscarded = new AtomicInteger(0);
        if (discards.size() > 0) {
            sb.append("discarded [" + discards.get(0));
            IntStream.range(1, discards.size()).forEach(_j -> sb.append(", " + discards.get(_j)));
            totalDiscarded.set(discards.stream().mapToInt(Integer::intValue).sum());
            sb.append("] = " + totalDiscarded);
            System.out.println(sb);

            totalSum.addAndGet(totalDiscarded.get());
        }

        totalSum.addAndGet(totalArrived.get());

        String output;
        if (totalSum.equals(totalSent.get())) {
            output = String.format("sent %d === got %d = %d + discarded %d", totalSent.get(), totalArrived.get(), totalSum.get(), totalDiscarded.get());
        } else {
            output = String.format("WRONG sent %d !== got %d = %d + discarded %d", totalSent.get(), totalArrived.get(), totalSum.get(), totalDiscarded.get());
        }

        System.out.println(output);
    }
}