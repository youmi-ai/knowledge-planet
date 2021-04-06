package ai.youmi.array;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Verify add method is non thread security in the multi-thread
 * @author Dongchan Year
 */
public class ArrayListTest {

    private final static ExecutorService pool = Executors.newFixedThreadPool(1000);
    private static ArrayList<Integer> list = new ArrayList<>();

    public static void main(String[] args) {
        for (int i = 0; i < 10000; i++) {
            pool.submit(new IncreaseTask());
        }
        if (!pool.isTerminated()) {
            try {
                Thread.sleep(1000 * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Total execute number: " + 10000 * 100);
        System.out.println("Actual execute number: " + list.size());
    }

    private static class IncreaseTask extends Thread {
        @Override
        public void run() {
            System.out.println("ThreadId: " + Thread.currentThread().getId() + " start !");
            for (int i = 0; i < 100; i++) {
                list.add(i);
            }
            System.out.println("ThreadId: " + Thread.currentThread().getId() + " finish !");
        }
    }
}
