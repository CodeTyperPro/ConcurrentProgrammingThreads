package done;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class StockExchangeHashMap {
    /*
    * 🏠 Homework: simulate a stock exchange.
    * The prices (a floating point number) of stocks (3 capital letters) are stored in a map,
    * each stock starts from $100.
    *
    * We have 100 brokers who randomly buy and sell
    * stocks for 10000 rounds.
    *
    * If a broker buys a stock, its price goes up by 1%, if he/she sells it,
    * then it goes down by 1%.
    *
    * Use Collections.synchronizedMap() (Links to an external site.) to ensure thread safety of the map.
    * */
    public static void main(String[] args) {
        Map<String, Double> stock = Collections.synchronizedMap(new HashMap<>());
        ArrayList<String> names = new ArrayList<>(Arrays.asList("BUD", "NYS", "LDA", "SHH"));

        Double defaultValue = 100.0;
        IntStream.range(0, names.size()).forEach(_i -> {
            stock.put(names.get(_i), defaultValue);
        });

        AtomicBoolean alive = new AtomicBoolean(true);
        final int THREAD_COUNT = 100, ELEMS_PER_THREAD = 10_000;
        ExecutorService es = Executors.newFixedThreadPool(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            int finalI = i;
            es.submit(() -> {
                IntStream.range(0, ELEMS_PER_THREAD).forEach(__ -> {
                    int index = ThreadLocalRandom.current().nextInt(0, names.size());
                    int operation = ThreadLocalRandom.current().nextInt(0, 2);

                    boolean isBuy = operation == 0;
                    String str;
                    Double newValue = stock.get(names.get(index));
                    if (isBuy) {
                        str = String.format("Broker [%d] bought a stock [%s]\n", (finalI + 1), names.get(index));
                        newValue -= 1.0;
                    } else {
                        str = String.format("Broker [%d] sold a stock [%s]\n", (finalI + 1), names.get(index));
                        newValue += 1.0;
                    }

                    stock.put(names.get(index), newValue);

                    //System.out.println(str);
                });
            });
        }

        Thread thread = new Thread(() -> {
            while (alive.get()) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, Double> sto : stock.entrySet()) {
                    String str = String.format("[%s -> %.0f%%]\n", sto.getKey(), sto.getValue());
                    sb.append(str);
                }

                System.out.println(sb);

                try {
                    Thread.sleep(100); // 100
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        thread.start();
        es.shutdown();

        try {
            es.awaitTermination(5, TimeUnit.SECONDS);
            alive.set(false);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            thread.join();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
