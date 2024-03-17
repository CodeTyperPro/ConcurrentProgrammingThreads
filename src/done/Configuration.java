package done;

import static java.lang.Math.abs;

public class Configuration {
    private long duration, startTime, endTime;
    public static volatile int numberOfWorkers = 0;
    final int MINIMUM_NUM_WORKERS, MAXIMUM_NUM_WORKERS;

    public Configuration() {
        synchronized (this) {
            this.duration = 0;
            this.MINIMUM_NUM_WORKERS = 2;
            this.MAXIMUM_NUM_WORKERS = 10;
        }
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getNumberOfWorkers() {
        return numberOfWorkers;
    }

    public void increaseNumberOfWorkers() {
        synchronized (this) {
            numberOfWorkers++;
        }
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void report() {
        duration = abs(this.endTime - this.startTime);
        System.out.println("Duration: " + duration);
        System.out.println("Number of workers: " + getNumberOfWorkers());
    }
}
