import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;
import java.util.Collections;

public class Test
{
    public static void nonSyncIterate(Collection<Integer> elements, int number)
    {
        //Iterator<Integer> iterator = elements.iterate();
        //while (iterator.hasNext()) { Integer e = iterator.next(); }
        for (Integer e : elements) {
            System.out.println(e + " " + number);
        }
    }
    public static void syncIterate(Collection<Integer> elements, int number)
    {
        synchronized (elements) {
            nonSyncIterate(elements, number);
        }
    }
    public static void main(String[] args)
    {
        Thread threads[] = new Thread[2];
        List<Integer> origElements = new ArrayList<>();
        //List<Integer> elements = new LinkedList<>();
        //Collection<Integer> elements = new Vector<>();
        Collection<Integer> elements = Collections.synchronizedCollection(origElements);
        //List<Integer> elements = Collections.synchronizedList(origElements);
        for (int i = 0; i < 2; i++) {
            int finali = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    syncIterate(elements, finali);
                }
            });
        }
        for (int i = 0; i < 2; i++) threads[i].start();
        for (int i = 0; i < 100000; i++) {
            elements.add(i);
        }
        try {
            for (int i = 0; i < 2; i++) {
                threads[i].join();
            }
        } catch (InterruptedException e) {}
    }
}