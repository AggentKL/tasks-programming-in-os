import java.util.Random;
import java.util.stream.IntStream;

public class Main {
    static final int ARR_SIZE = 10000;
    public static void main(String[] args) {
        int[] first = generateArr(ARR_SIZE);
        int[] second = generateArr(ARR_SIZE);
        long duration = 0;
        duration = RunCalculation(first, second, true, new CalcParallel());
        System.out.printf("Duration Async Method WITH sleep : %d miliseconds\n", duration);
        duration = RunCalculation(first, second, false, new CalcParallel());
        System.out.printf("Duration Async Method WITHOUT sleep : %d miliseconds\n", duration);
        duration = RunCalculation(first, second, true, new CalcSequential());
        System.out.printf("Duration Sync Method WITH sleep : %d miliseconds\n", duration);
        duration = RunCalculation(first, second, false, new CalcSequential());
        System.out.printf("Duration Sync Method WITHOUT sleep : %d miliseconds\n", duration);

        System.out.println("\nMili are not precise. Use nano - they more precise");
    }

    static long RunCalculation(int[] first, int[] second, boolean isDelay, CalcRunner runner){
        long start = System.currentTimeMillis();
        runner.Run(first, second, isDelay);
        long end = System.currentTimeMillis();
        return end-start;
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
    private interface CalcRunner{
        int[] Run(int[] first, int[] second,boolean doSleep);
    }
    private static class CalcSequential implements CalcRunner{

        @Override
        public int[] Run(int[] first, int[] second, boolean doSleep) {
            int[] syncResult = new int[ARR_SIZE];
            for(int i = 0; i < ARR_SIZE; i++){
                syncResult[i] = first[i]*second[i];
                safe1ms(doSleep);
            }
            return syncResult;
        }
    }
    private static class CalcParallel implements CalcRunner{

        @Override
        public int[] Run(int[] first, int[] second, boolean doSleep) {
            int[] asyncResult = new int[ARR_SIZE];

            IntStream.range(0, ARR_SIZE).parallel().forEach(i -> {
                asyncResult[i] = first[i] * second[i];
                safe1ms(doSleep);
            });
            return asyncResult;
        }
    }
}