package done;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class SyncWithSyncList {
    public static void main(String[] args) {
        List<Integer> syncOriginal = Collections.synchronizedList(new ArrayList<>());
        List<Integer> syncResult = Collections.synchronizedList(new ArrayList<>());

        final int THREAD_COUNT = 2, ELEMS_PER_THREAD = 100_000;
        final int MAX_LENGTH = THREAD_COUNT * ELEMS_PER_THREAD;
        IntStream.range(0, MAX_LENGTH).forEach(value -> {
            syncOriginal.add(value + 1);
        });

        ExecutorService es = Executors.newFixedThreadPool(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            es.submit(() -> {
                IntStream.range(0, ELEMS_PER_THREAD).forEach(__ -> {
                    if (!syncOriginal.isEmpty()) {
                        int value = syncOriginal.get(0);
                        syncOriginal.remove(0); // removes first elements
                        if (syncResult.isEmpty()) {
                            syncResult.add(value);
                        } else {
                            syncResult.add(0, value);
                        }
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
        sb.append("OUTPUT -> 1st 100 elements: \n");

        int res_len = syncResult.size();
        System.out.println("LENGTH -> " + res_len);

        final int SIZE_ARRAYS = 100;
        IntStream.range(0, Math.min(SIZE_ARRAYS, syncResult.size())).forEach(_j -> {
            sb.append("[" + syncResult.get(_j) + "] ");
        }); sb.append("\n");
        System.out.println(sb);
    }
}
