import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Homework: Custom Blocking Queue
 *
 * Implement a very simple blocking queue using the wait & notify technique.
 *  Use one our earlier assignments to test it.
 *  It is enough to implement the take() (Links to an external site.) method.
 *  Since the queue is not aware of the threads waiting for the queue,
 *  use the notifyAll() (Links to an external site.) method to wake up any thread.
 *  Extend interface Queue (Links to an external site.) and any of its implementations,
 *  e.g. ArrayDeque (Links to an external site.).
 *
 */

public class CustomBlockingQueue<T> extends ArrayDeque<T> {

    public CustomBlockingQueue() {
        super();
    }

    public CustomBlockingQueue(int numElem) {
        super(numElem);
    }

    public synchronized T take() throws InterruptedException {
        while (isEmpty()) {
            wait();
        }

        T element = poll();
        notifyAll();
        return element;
    }

    public synchronized void put(T element) {
        super.offer(element);
        notifyAll();
    }
}

