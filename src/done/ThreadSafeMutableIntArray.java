package done;

import java.util.Arrays;
import java.util.stream.IntStream;

public class ThreadSafeMutableIntArray {
    private int[] array;
    private Object[] locks;
    private final int CAPACITY;

    public ThreadSafeMutableIntArray(int capacity) {
        this.CAPACITY = capacity;
        array = new int[this.CAPACITY];
        Arrays.fill(array, 0);
        locks = new Object[this.CAPACITY];

        IntStream.range(0, this.CAPACITY).forEach(index -> {
            locks[index] = new Object();
        });
    }

    public int get(int idx) {
        synchronized (locks[idx]) {
            return array[idx]; // Object or int???
        }
    }

    public void set(int idx, int newValue) {
        // use synchronization with the locks object at index idx.
        synchronized (locks[idx]) {
            array[idx] = newValue;
        }
    }

    public static void main(String[] args) {

        final int SIZE = 10;
        Thread[] threads = new Thread[SIZE];

        final int SIZE_THREADSAFE = 2;
        ThreadSafeMutableIntArray safeMutableIntArray = new ThreadSafeMutableIntArray(SIZE_THREADSAFE);

        long startTimeThreads = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            final int finalI = i;
            threads[i] = new Thread(() -> {
                IntStream.range(0, 10_000_000 + 1).forEach(_j -> {
                    final int pos = finalI < SIZE / 2 ? 0 : 1;
                    safeMutableIntArray.set(pos, _j);
                });
            });
        }

        IntStream.range(0, SIZE).forEach(_j -> {
            threads[_j].start();
        });

        try {
            for (int i = 0; i < SIZE; i++) {
                threads[i].join();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("OUTPUT: \n");

        IntStream.range(0, SIZE_THREADSAFE).forEach(_j -> {
            sb.append("[" + safeMutableIntArray.get(_j) + "] ");
        }); sb.append("\n");

        System.out.println(sb);

        long endTimeThreads = System.nanoTime() - startTimeThreads;
        System.out.println("Estimated running time (" + SIZE + " Threads) = " + endTimeThreads);
    }
}

// Do you experience anything strange? Can you do something about it?
// Yes, both remain 0. Why_???
//
