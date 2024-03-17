package done.assignment;

import java.util.concurrent.atomic.AtomicInteger;

public class DataMover2Result {
    private final AtomicInteger data, forwarded, count;

    public DataMover2Result() {
        this.count = new AtomicInteger(0);
        this.data = new AtomicInteger(0);
        this.forwarded = new AtomicInteger(0);
    }

    public int getCount() {
        return this.count.get();
    }

    public int getData() {
        return data.get();
    }

    public void setData(int data) {
        this.data.addAndGet(data);
    }
    public void incrementCount() {
        this.count.addAndGet(1);
    }

    public int getForwarded() {
        return this.forwarded.get();
    }

    public void incrementForward() {
        this.forwarded.addAndGet(1);
    }
}
