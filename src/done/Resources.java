package done;

public class Resources {
    int GOLD_MINE_CAPACITY;
    int GOLD_OWNED_WORKERS;
    final int GOLD_PER_HOUSE;
    int NUMBER_HOUSES_BUILT;

    public Resources() {
        this.GOLD_MINE_CAPACITY = 120;
        this.GOLD_OWNED_WORKERS = 0;
        this.NUMBER_HOUSES_BUILT = 0;
        this.GOLD_PER_HOUSE = 20;
    }

    public int getGOLD_MINE_CAPACITY() {
        return GOLD_MINE_CAPACITY;
    }

    public int getGOLD_OWNED_WORKERS() {
        return GOLD_OWNED_WORKERS;
    }

    public int getNUMBER_HOUSES_BUILT() {
        return NUMBER_HOUSES_BUILT;
    }

    public void buildHouse() {
        this.GOLD_OWNED_WORKERS -= this.GOLD_PER_HOUSE;
        this.NUMBER_HOUSES_BUILT++;
    }

    public synchronized void increaseGolds() {
        this.GOLD_OWNED_WORKERS++;
        this.GOLD_MINE_CAPACITY--;

        System.out.println(" ---------- State --------------- ");
        report();

        System.out.println("Gold owned workers = " + this.getGOLD_OWNED_WORKERS());
        System.out.println("Gold mine capacity = " + this.getGOLD_MINE_CAPACITY());

        System.out.println(" ---------- [x_x] --------------- ");
    }

    public void report() {
        System.out.println("Number of houses built: " + getNUMBER_HOUSES_BUILT());
    }
}
