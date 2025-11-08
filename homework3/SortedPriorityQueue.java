
public class SortedPriorityQueue {

    int[] data;
    int elementCount = 0;

    public SortedPriorityQueue(int size) {
        data = new int[size];
    }

    public void offer(int element) {
        if (elementCount >= data.length) {
            throw new RuntimeException("Queue size exceeded.");
        }

        // find location for insertion
        int insertionIndex = findElementIndex(element);
        shiftElements(insertionIndex);
        data[insertionIndex] = element;
        elementCount++;
    }

    public int poll() {
        if (elementCount == 0) {
            throw new RuntimeException("Queue is empty.");
        }
        return data[--elementCount];
    }

    private int findElementIndex(int value) {
        int index = 0;
        while (index < elementCount && value >= data[index] && data[index] != value) {
            index++;
        }
        return index;
    }

    // Shift all of the data elements from the index
    private void shiftElements(int index) {
        for (int i = elementCount - 1; i >= index; i--) {
            data[i + 1] = data[i];
        }
    }

    public static void main(String[] args) {
        final SortedPriorityQueue queue = new SortedPriorityQueue(10);
        queue.offer(6);
        queue.offer(5);
        queue.offer(1);
        queue.offer(2);
        queue.offer(1);

        queue.printQueue();

        System.out.println("poll:" + queue.poll());
        System.out.println("poll:" + queue.poll());
        System.out.println("poll:" + queue.poll());
        System.out.println("poll:" + queue.poll());
        System.out.println("poll:" + queue.poll());

    }

    private void printQueue() {
        for (int i = 0; i < elementCount; i++) {
           System.out.println(String.format("%d - %d", i, data[i]));
        }
    }
}
