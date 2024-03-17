package done;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Switcheroo {

    public static void main(String[] args) {

        final int SIZE = 10;
        Thread[] threads = new Thread[SIZE];
        ExecutorService es = Executors.newFixedThreadPool(SIZE);

        final int SIZE_ARRAYS = 100;
        final int VALUE = 1_000;
        AtomicInteger[] array = new AtomicInteger[SIZE_ARRAYS];

        IntStream.range(0, SIZE_ARRAYS).forEach(index -> {
            array[index] = new AtomicInteger(VALUE);
        });

        long startTimeThreads = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            // final int finalI = i;
            es.submit(() -> {
                IntStream.range(0, 10_000).forEach(_j -> {
                    int index1 = ThreadLocalRandom.current().nextInt(0, SIZE_ARRAYS);
                    int index2 = ThreadLocalRandom.current().nextInt(0, SIZE_ARRAYS);

                    while (index1 == index2) {
                        index1 = ThreadLocalRandom.current().nextInt(0, SIZE_ARRAYS);
                        index2 = ThreadLocalRandom.current().nextInt(0, SIZE_ARRAYS);
                    }

                    final int pos1 = index1, pos2 = index2;

                    int ammount = ThreadLocalRandom.current().nextInt(0, array[pos1].get());

                    // Both array[pos1] and array[pos2] are AtomicIntegers
                    array[pos1].getAndAdd(-ammount); // Is there getAnd Decrement
                    array[pos2].getAndAdd(ammount);
                });
            });
        }


        es.shutdown();

        try {
            es.awaitTermination(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        StringBuilder sb = new StringBuilder();
        sb.append("OUTPUT: \n");
        int expectedSum = VALUE * SIZE_ARRAYS;

        sb.append("Expected sum: " + expectedSum + "\n");
        int returnedSum = IntStream.range(0, SIZE_ARRAYS).map(i -> array[i].get()).sum();
        sb.append("Returned sum: " + returnedSum + "\n");

        System.out.println(sb);

        long endTimeThreads = System.nanoTime() - startTimeThreads;
        System.out.println("Estimated running time (" + SIZE + " Threads) = " + endTimeThreads);
    }
}
