package done;

import java.math.BigInteger;
import java.util.ArrayList;

public class MainHomework3 {
    /*
        🏠 Homework: check that execution using multiple threads causes significant speedup.
        Add up the numbers of the interval 1..1_000_000_000 on a thread.
        Measure how long it takes using System.nanoTime() (Links to an external site.).

        Then start 10 threads, which add up the numbers of the interval 1..1_000_000_000:
        The first thread works on the interval 1..100_000_000, the second thread works on
         the interval 100_000_001..200_000_000 etc.
        Put the sum into a static variable.
        Calculate how long it takes using System.nanoTime() and print it at the end of the execution of the thread.
    */

    static BigInteger sum = BigInteger.ZERO, sum10 = BigInteger.ZERO;
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        Thread thread = new Thread(() -> {
            for (int i = 1; i <= 1_000_000_000; i++) {
                sum = sum.add(BigInteger.valueOf(i));
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Total time (1 Thread): " + estimatedTime);

        /*
        System.out.println("LIST: \n");
        for (Integer x : list) {
            System.out.println(x + " ");
        }
        System.out.println();
        */

        final int size = 10;
        Thread[] threads = new Thread[size];

        long startTimeThreads = System.nanoTime();
        for (int i = 0; i < size; i++) {
            final int finalI = i;
            threads[i] = new Thread(() -> {
                BigInteger localSum = BigInteger.ZERO;
                for (int j = 1 + 1_000_000_000 * finalI; j <= 1_000_000_000 + 1_000_000_000 * finalI; j++) {
                    localSum = localSum.add(BigInteger.valueOf(j));
                }

                synchronized (sum10) {
                    sum10 = sum10.add(localSum);
                }
            });

            threads[i].start();
        }

        try {
            for (int i = 0; i < size; i++) {
                threads[i].join();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        long endTimeThreads = System.nanoTime() - startTimeThreads;
        System.out.println("Estimated running time (10 Threads) = " + endTimeThreads);

        System.out.println("1st sum = " + sum.longValue());
        System.out.println("2nd sum = " + sum10.longValue());
    }
}