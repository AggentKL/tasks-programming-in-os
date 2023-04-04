import java.util.Random;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        int[] first = generateArr(10000);
        int[] second = generateArr(10000);
        runSyncMethod(first, second);
        runSyncMethodWithSleep(first, second);
        runAsyncMethod(first, second);
        runAsyncMethodWithSleep(first, second);

        System.out.println("Mili are not precise. Use nano - they more precise");
    }
    static int[] CalcCycle(int[] first, int[] second, boolean doSleep){
        int[] syncResult = new int[10000];
        for(int i = 0; i < 10000; i++){
            syncResult[i] = first[i]*second[i];
            safe1ms(doSleep);
        }
        return syncResult;
    }

    static void runSyncMethod(int[] first, int[] second){
        long start = System.currentTimeMillis();
        CalcCycle(first, second, false);
        long end = System.currentTimeMillis();
        long duration = end-start;
        System.out.printf("Duration Sync Method WITHOUT sleep : %d miliseconds\n", duration);
    }

    static void runSyncMethodWithSleep(int[] first, int[] second){
        long start = System.currentTimeMillis();
        CalcCycle(first, second, true);
        long end = System.currentTimeMillis();
        long duration = end-start;
        System.out.printf("Duration Sync Method WITH sleep : %d miliseconds\n", duration);
    }

    static void runAsyncMethod(int[] first, int[] second){
        long start = System.currentTimeMillis();
        CalcParralel(first, second, false);
        long end = System.currentTimeMillis();
        long duration = end-start;
        System.out.printf("Duration Async Method WITHOUT sleep : %d miliseconds\n", duration);
    }

    static void runAsyncMethodWithSleep(int[] first, int[] second){
        long start = System.currentTimeMillis();
        CalcParralel(first, second, true);
        long end = System.currentTimeMillis();
        long duration = end-start;
        System.out.printf("Duration Async Method WITH sleep : %d miliseconds\n", duration);
    }

    static int[] CalcParralel(int[] first, int[] second,boolean doSleep){
        int[] asyncResult = new int[10000];

        IntStream.range(0, 10000).parallel().forEach(i -> {
            asyncResult[i] = first[i] * second[i];
            safe1ms(doSleep);
        });
        return asyncResult;
    }

    private static void safe1ms(boolean doSleep) {
        if (!doSleep) {
            return;
        }

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            // Hello Igor Anatolijovich
        }
    }
    private static int[] generateArr(int size) {
        int[] array = new int[size];
        Random rand = new Random();

        for (int i = 0; i < size; i++) {
            array[i] = rand.nextInt(100);
        }

        return array;
        }
}