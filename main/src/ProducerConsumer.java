import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ProducerConsumer {
    private static int bufferCapacity = 10;

    public static void main(String[] args) {
        BlockingQueue<String> buffer = new ArrayBlockingQueue<>(bufferCapacity);
        Thread producerThread = new Thread(new Producer(buffer, bufferCapacity));
        Thread consumerThread = new Thread(new Consumer(buffer, bufferCapacity));
        System.out.println(buffer.remainingCapacity());

        producerThread.start();
        consumerThread.start();
    }
}

class Producer implements Runnable {
    BlockingQueue<String> buffer;
    int bufferCapacity;

    public Producer(BlockingQueue<String> buffer, int bufferCapacity) {
        this.buffer = buffer;
        this.bufferCapacity = bufferCapacity;
    }

    @Override
    public void run() {
        for (int i = 0; i < bufferCapacity; i++) {
            try {
                System.out.println("Produced: " + i);
                buffer.put(String.valueOf(i));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Consumer implements Runnable {
    BlockingQueue<String> buffer;
    int bufferCapacity;
    int taken = 0;

    public Consumer(BlockingQueue<String> buffer, int bufferCapacity) {
        this.buffer = buffer;
        this.bufferCapacity = bufferCapacity;
    }

    @Override
    public void run() {
        while (taken < bufferCapacity) {
            try {
                String item = buffer.take();
                System.out.println("Consumed: " + item);
                taken++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
