package done;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class InefficientSorter {

    public static void main(String[] args) {
        final int SIZE = 10;
        Thread[] threads = new Thread[SIZE];
        ExecutorService es = Executors.newFixedThreadPool(SIZE);

        final int SIZE_ARRAYS = 100;
        final int VALUE = 100;

        AtomicInteger[] array = new AtomicInteger[SIZE_ARRAYS];
        IntStream.range(0, SIZE_ARRAYS).forEach(index -> {
            array[index] = new AtomicInteger(ThreadLocalRandom.current().nextInt(0, VALUE + 1));
        });

        int[] copyArray = new int[SIZE_ARRAYS];
        IntStream.range(0, SIZE_ARRAYS).forEach(index -> {
            copyArray[index] = array[index].get();
        });

        long startTimeThreads = System.nanoTime();

        for (int i = 0; i < SIZE; i++) {
            final int finalI = i;
            es.submit(() -> {
                IntStream.range(0, 10_000).forEach(_j -> {
                    int index1 = ThreadLocalRandom.current().nextInt(0, SIZE_ARRAYS);
                    int index2 = ThreadLocalRandom.current().nextInt(0, SIZE_ARRAYS);

                    while (index1 == index2) {
                        index1 = ThreadLocalRandom.current().nextInt(0, SIZE_ARRAYS);
                        index2 = ThreadLocalRandom.current().nextInt(0, SIZE_ARRAYS);
                    }

                    final int pos1 = index1, pos2 = index2;

                    // Both array[pos1] and array[pos2] are AtomicIntegers
                    if (pos1 < pos2 && array[pos1].get() > array[pos2].get()) {
                        //Collections.swap(array, pos1, pos2);
                        AtomicInteger aux = new AtomicInteger(array[pos1].get());
                        array[pos1].set(array[pos2].get());
                        array[pos2].set(aux.get());
                    }
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

        sb.append("\nORIGINAL ARRAY: ");
        for (int i = 0; i < copyArray.length; i++) {
            sb.append("[" + copyArray[i] + "] ");
        } sb.append("\n");

        sb.append("\nMODIFIED ARRAY: ");
        for (int i = 0; i < array.length; i++) {
            sb.append("[" + array[i] + "] ");
        }

        System.out.println(sb);

        long endTimeThreads = System.nanoTime() - startTimeThreads;
        System.out.println("\n\nEstimated running time (" + SIZE + " Threads) = " + endTimeThreads);
    }
}
