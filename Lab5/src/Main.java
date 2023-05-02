import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    final static String file = "TestPrintingThreadText.txt";
    private static FileOutputStream printThread;
    private static volatile int iterator = 0;
    private static volatile int lastIteratorPrint = 0;

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        printThread = new FileOutputStream(file);

        Thread thread1 = new Thread(Main::thread1Run);
        Thread thread2 = new Thread(Main::thread2Run);
        Thread thread3 = new Thread(Main::thread3Run);

        thread1.start();
        thread2.start();
        thread3.start();
    }

    private static String currentTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime timeNow = LocalDateTime.now();
        return dtf.format(timeNow);
    }


    private static synchronized void printCurrentIterator(int thread) {
        if (iterator == lastIteratorPrint) return;

        try {
            printThread.write(
                    String.format("Thread №%d | Iterator: %d | Time: %s\n", thread, iterator, currentTime()).getBytes()
            );
            lastIteratorPrint = iterator;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void thread1Run() {
        int thisIterator = 0;
        while (iterator <= 240) {
            printCurrentIterator(1);

            thisIterator++;
            iterator = thisIterator;

            try {
                Thread.sleep(250);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void thread2Run() {
        while (iterator <= 240) {
            printCurrentIterator(2);
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void thread3Run() {
        while (iterator <= 240) {
            printCurrentIterator(3);
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}