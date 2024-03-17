package done;

import java.util.Random;

public class ThreadCraft {
    static final String names[] = {"Miners", "Builders", "Login"};
    private static volatile Resources resources;
    private static volatile Configuration configuration;
    static abstract class DefaultThread extends Thread {
        private boolean running = false;
        public DefaultThread(String name) {
            super(name);

            synchronized (this) {
                Configuration.numberOfWorkers++;
            }
        }

        public synchronized void action() {
            this.running = true;
            super.start();
        }

        public synchronized void terminate() {
            this.running = false;
        }

        public synchronized boolean isRunning() {
            return running;
        }

        public synchronized void checkStatus() {
            if (resources.getGOLD_MINE_CAPACITY() <= 0) {
                this.terminate();
            }
        }

        @Override
        public void run() {
            // ...
        }

        public void sleepForMsec(int msec) throws InterruptedException {
            Thread.sleep(msec);
        }
    }

    static class Mine extends DefaultThread{
        public Mine() {
            super(names[0]);
        }

        public synchronized void mineAction() {
            super.action();
        }

        @Override
        public void run() {
            while(isRunning()) {
                if (resources.GOLD_MINE_CAPACITY > 0) {
                    resources.increaseGolds();
                }
                checkStatus();
            }
        }
    }

    static class Build extends DefaultThread{
        public Build() {
            super(names[1]);
        }

        public synchronized void buildAction() {
            super.action();
        }

        @Override
        public void run() {
            while(isRunning()) {;
                if (resources.getGOLD_OWNED_WORKERS() >= resources.GOLD_PER_HOUSE) {
                    resources.buildHouse(); // Build house
                    System.out.println("House built!");
                }
                checkStatus();
            }
        }
    }

    static class Login extends DefaultThread{
        public Login() {
            super(names[2]);
        }

        public synchronized void loggingAction() {
            super.action();
        }

        @Override
        public void run() {
            while(isRunning()) {
                try {
                    this.sleepForMsec(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                synchronized (this) {
                    System.out.println(" ---------- Current state --------------- ");
                    resources.report();
                    configuration.report();
                    System.out.println(" ----------     [x_x]     --------------- ");
                }
                checkStatus();
            }
        }
    }

    public static void main(String[] args) {
        resources = new Resources();
        configuration = new Configuration();

        configuration.setStartTime(System.nanoTime());

        Random random = new Random(System.nanoTime());
        int numThreads = random.nextInt(configuration.MINIMUM_NUM_WORKERS, configuration.MAXIMUM_NUM_WORKERS);

        // Thread[] threads = new Thread[numThreads];

        long startTimeThreads = System.nanoTime();

        Mine mine = new Mine();
        Build build = new Build();
        Login login = new Login();

        mine.mineAction();
        build.buildAction();
        login.loggingAction();

        try {
            mine.join();
            build.join();
            login.join();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println("Simulation over!");
            configuration.setEndTime(System.nanoTime());
            resources.report();
        }
    }
}


/*
*         for (int i = 0; i < numThreads; i++) {
            final int finalI = i;

            int typeThread = random.nextInt(0, 2);
            switch (typeThread) {
                case 0:
                    threads[i] = new Mine(); break;
                case 1:
                    threads[i] = new Build(); break;
            }
            threads[i].start();
        }
* */