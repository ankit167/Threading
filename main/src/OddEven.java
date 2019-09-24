import java.util.Scanner;

/**
 * Print Odd and Even Integers using 2 threads, upto 'n' times.
 * The threads should work in sync, with one thread printing odd
 * integers, and the other thread printing even integers.
 *
 * Input: 5
 * Output:
 *         Thread Odd:1
 *         Thread Even:2
 *         Thread Odd:3
 *         Thread Even:4
 *         Thread Odd:5
 *
 */
public class OddEven {
    private static Scanner sb;

    public static void main(String[] args) {
        Printer printer = new Printer();
        sb = new Scanner(System.in);
        int n = sb.nextInt();

        Thread t1 = new Thread(new OddEvenTask(n, printer, false), "Thread Odd");
        Thread t2 = new Thread(new OddEvenTask(n, printer, true), "Thread Even");
        t1.start();
        t2.start();
    }
}

class OddEvenTask implements Runnable {
    private int max;
    private Printer printer;
    private boolean isEvenNumber;

    public OddEvenTask(int max, Printer printer, boolean isEvenNumber) {
        this.max = max;
        this.printer = printer;
        this.isEvenNumber = isEvenNumber;
    }

    @Override
    public void run() {
        int number = isEvenNumber? 2:1;
        while (number <= max) {
            if (isEvenNumber) {
                printer.printEven(number);
            } else {
                printer.printOdd(number);
            }
            number += 2;
        }
    }
}

class Printer {
    private volatile boolean isOdd;

    public synchronized void printEven(int number) {
        while (!isOdd) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + ":" + number);
        isOdd = false;
        notify();
    }

    public synchronized void printOdd(int number) {
        while (isOdd) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + ":" + number);
        isOdd = true;
        notify();
    }
}
