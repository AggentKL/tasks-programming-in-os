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

        Thread thread1 = new Thread(() -> threadRun(1, 250));
        Thread thread2 = new Thread(() -> threadRun(2, 500));
        Thread thread3 = new Thread(() -> threadRun(3, 1000));

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

    private static void threadRun(int threadNumber, int waitMilli) {
        int thisIterator = 0;
        while (iterator <= 240) {
            printCurrentIterator(threadNumber);

            if (threadNumber == 1) {
                thisIterator++;
                iterator = thisIterator;
            }

            try {
                Thread.sleep(waitMilli);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}