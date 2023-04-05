import java.util.Scanner;
import java.util.concurrent.*;


public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter index of fibbanachi num: ");
        long index = scan.nextLong();

        System.out.println("Waiting for async fib(" + index + ") function");
        Future<Long> resultFuture = ASyncCalcFib(index);
        while (!resultFuture.isDone()){
            System.out.println("Skuratovskyi: I'm waiting...");
            Thread.sleep(1000);
        }
        long result = resultFuture.get();
        System.out.println("Result: " + result);
    }

    public static Future<Long> ASyncCalcFib(long index) throws ExecutionException {
        CompletableFuture<Long> completableFuture = new CompletableFuture<>();

        ExecutorService pool = Executors.newCachedThreadPool();
        pool.submit(() -> {
            completableFuture.complete(fibbanachi(index));
            return null;
        });
        pool.shutdown();

        return completableFuture;
    }

    public static Long fibbanachi(Long x) {
        if (x <= 1) {
            return x;
        }

        return fibbanachi(x - 1) + fibbanachi(x - 2);
    }
}
