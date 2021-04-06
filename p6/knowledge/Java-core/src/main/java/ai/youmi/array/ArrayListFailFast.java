package ai.youmi.array;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Fail-Fast systems abort operation as-fast-as-possible exposing failure immediately and
 * stopping the whole operation.
 * The program use iterator with iterator when modCount != expectedModCount export exception in multi-thread.
 *
 * @author Dongchan Year
 */
public class ArrayListFailFast {

    public static void main(String[] args) {

        ArrayList<Integer> array = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            array.add(i);
        }

        new ArrayListIterator(array).start();
        new ArrayListAdd(array).start();
    }

    private static class ArrayListAdd extends Thread {
        private ArrayList<Integer> list;

        public ArrayListAdd(ArrayList<Integer> list) {
            this.list = list;
        }

        @Override
        public void run() {
            for (int i = 0; i < 11; i++) {
                this.list.add(i);
                System.out.println("Loop add value to list: " + i);
            }
        }
    }

    private static class ArrayListIterator extends Thread {
        private ArrayList<Integer> list;

        public ArrayListIterator(ArrayList<Integer> list) {
            this.list = list;
        }

        @Override
        public void run() {
            for (Iterator<Integer> iterator = this.list.iterator(); this.list.iterator().hasNext(); ) {
                System.out.println("Iterator value: " + iterator.next());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
