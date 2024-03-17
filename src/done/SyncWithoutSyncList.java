package done;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class SyncWithoutSyncList {
    public static void main(String[] args) {
        List<Integer> original = new ArrayList<>();
        List<Integer> result = new ArrayList<>();

        final int THREAD_COUNT = 2, ELEMS_PER_THREAD = 100_000;
        final int MAX_LENGTH = THREAD_COUNT * ELEMS_PER_THREAD;
        IntStream.range(0, MAX_LENGTH).forEach(value -> {
            original.add(value + 1);
        });

        ExecutorService es = Executors.newFixedThreadPool(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            es.submit(() -> {
                try {
                    IntStream.range(0, ELEMS_PER_THREAD).forEach(__ -> {
                        synchronized (original) {
                            if (!original.isEmpty()) {
                                int value = original.get(0);
                                original.remove(0); // removes first elements
                                synchronized (result) {
                                    if (result.isEmpty()) {
                                        result.add(value);
                                    } else {
                                        result.add(0, value);
                                    }
                                }
                            }
                        }
                    });
                } catch (ConcurrentModificationException e) {
                    System.out.println(e.getMessage());
                }
            });
        }

        es.shutdown();

        try {
            es.awaitTermination(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        synchronized (result) {
            StringBuilder sb = new StringBuilder();
            sb.append("OUTPUT -> 1st 100 elements: \n");
            int res_len = result.size();
            System.out.println("LENGTH -> " + res_len);

            final int SIZE_ARRAYS = 100;
            IntStream.range(0, Math.min(SIZE_ARRAYS, result.size())).forEach(_j -> {
                sb.append("[" + result.get(_j) + "] ");
            }); sb.append("\n");
            System.out.println(sb);
        }
    }
}
