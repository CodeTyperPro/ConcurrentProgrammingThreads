package done.assignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class DataMover2 {

    // Class variables
    public static AtomicInteger arrivalCount, totalSent, totalArrived;
    public static ExecutorService pool;
    public static List<BlockingQueue<Integer>> queues;
    public static List<Future<DataMover2Result>> moverResults;
    public static int SIZE;
    static final int MAX_CAPACITY = 100;
    public static final String[] DEFAULT_ARGS = {"123", "111", "256", "404"};

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Check for command-line arguments
        SIZE = args.length;
        if (SIZE < 2) {
            SIZE = DEFAULT_ARGS.length;
            args = DEFAULT_ARGS;
            System.out.println("Command-line arguments were not passed. DEFAULT values set instead ...");
        }

        // Initialization of atomic integers
        arrivalCount = new AtomicInteger(0);
        totalSent = new AtomicInteger(0);
        totalArrived = new AtomicInteger(0);

        moverResults = Collections.synchronizedList(new ArrayList<Future<DataMover2Result>>());

        // Set array size and initialize arrays
        AtomicInteger counter = new AtomicInteger(5 * SIZE);
        final Integer MAX_ARRIVAL_COUNT = 5 * SIZE;
        final long[] waitTimeThread = new long[SIZE];

        // Fill
        String[] finalArgs = args;
        queues = Collections.synchronizedList(new ArrayList<>());

        final int LEN = Math.min(MAX_CAPACITY, SIZE); // 100 is the maximum size !!!

        final Integer MAX_BUFFER = 2 * LEN;
        IntStream.range(0, LEN).forEach(i -> {
            queues.add(new LinkedBlockingQueue<>());
            waitTimeThread[i] = Long.parseLong(finalArgs[i]);
        });

        pool = Executors.newFixedThreadPool(LEN);

        // Start tasks
        for (int i = 0; i < LEN; i++) {
            int finalI = i;

            Callable<DataMover2Result> result = () -> {
                final DataMover2Result res = new DataMover2Result();
                AtomicInteger x = new AtomicInteger(0);
                final Integer next = (finalI + 1) % queues.size();

                while (true) {
                    if (counter.get() <= 0) break;
                    String outputSuffix = "";
                    // sends x
                    if (queues.get(next).size() != MAX_BUFFER) {
                        x.set(ThreadLocalRandom.current().nextInt(0, 10_000));
                        queues.get(next).put(x.get()); // Blocking insert is 'put'!
                        totalSent.addAndGet(x.get());
                    }

                    outputSuffix = String.format("sends %d", x.get());
                    String outputPrefix = String.format("total\t%d/%d\t|\t#%d ", arrivalCount.get(), MAX_ARRIVAL_COUNT, finalI);
                    System.out.println(outputPrefix + outputSuffix);

                    final long timeOut = ThreadLocalRandom.current().nextInt(300, 1000);
                    final long deadLine = System.currentTimeMillis() + timeOut;
                    while (true) {
                        final long now = System.currentTimeMillis();
                        if (now >= deadLine) break;
                        while (queues.get(finalI).isEmpty()) {
                            outputSuffix = "got nothing ...";
                            outputPrefix = String.format("total\t%d/%d\t|\t#%d ", arrivalCount.get(), MAX_ARRIVAL_COUNT, finalI);
                            String output = outputPrefix + outputSuffix;
                            System.out.println(output);
                            break;
                        }

                        Integer takeInput = queues.get(finalI).poll(timeOut, TimeUnit.MILLISECONDS); // waits until an element is available
                        if (takeInput == null) break;
                        //outputPrefix = String.format("total\t%d/%d\t|\t#%d ", arrivalCount.get(), MAX_ARRIVAL_COUNT, finalI);
                        if (takeInput % queues.size() == finalI) {
                            res.incrementCount();
                            res.setData(takeInput);
                            arrivalCount.addAndGet(1);
                            counter.addAndGet(-1);

                            outputSuffix = String.format("got %d", takeInput);
                        } else {
                            queues.get(next).put(takeInput - 1);
                            res.incrementForward();
                            outputSuffix = String.format("forwards %d [%d]", takeInput, takeInput % queues.size());
                        }

                        outputPrefix = String.format("total\t%d/%d\t|\t#%d ", arrivalCount.get(), MAX_ARRIVAL_COUNT, finalI);
                        String output = outputPrefix + outputSuffix;
                        System.out.println(output);

                        if (counter.get() <= 0) {
                            return res;
                        }

                        break;
                    }

                    try { Thread.sleep(waitTimeThread[finalI]); } catch (InterruptedException e) { }
                }
                return res;
            };

            Future<DataMover2Result> task = pool.submit(result);
            moverResults.add(task);
        }

        // Wait for all tasks to complete
        for (Future<DataMover2Result> dataMover2Result : moverResults) {
            dataMover2Result.get();  // Wait for the task to complete
        }

        pool.shutdown(); // Graceful termination
        if (!pool.awaitTermination(30, TimeUnit.SECONDS)) { pool.shutdownNow(); }  // Forceful termination :)

        // Collect results after all tasks have completed
        synchronized (moverResults) {
            for (Future<DataMover2Result> dataMover2Result : moverResults) {
                DataMover2Result data = dataMover2Result.get();
                totalArrived.addAndGet(data.getData() + data.getForwarded());
            }
        }

        List<Integer> discards = Collections.synchronizedList(new ArrayList<>());
        synchronized (discards) {
            queues.forEach(queue -> queue.drainTo(discards));
        }

        // Print output
        StringBuilder sb = new StringBuilder();
        final AtomicInteger totalSum = new AtomicInteger(0);
        final AtomicInteger totalDiscarded = new AtomicInteger(0);
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
        if (totalSum.get() == totalSent.get()) {
            output = String.format("sent %d === got %d = %d + discarded %d", totalSent.get(), totalSum.get(), totalArrived.get(), totalDiscarded.get());
        } else {
            output = String.format("WRONG sent %d !== got %d = %d + discarded %d", totalSent.get(), totalSum.get(), totalArrived.get(), totalDiscarded.get());
        }

        System.out.println(output);
        // System.out.println("# of packages lost: " + (totalSent.get() - totalSum.get()));
    }
}