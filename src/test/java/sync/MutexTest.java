package sync;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MutexTest {

    @Test
    @DisplayName("여러 스레드가 동시에 접근해도 상호 배제가 보장된다")
    public void mutualExclusionTest() throws InterruptedException {
        int threadCount = 10;
        int iterationsPerThread = 50;
        Mutex mutex = new Mutex();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch end = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            Thread worker = new Thread(() -> {
                try {
                    start.await();
                    for (int j = 0; j < iterationsPerThread; j++) {
                        mutex.doSomething();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    end.countDown();
                }
            }, "thread-worker-" + i);
            worker.start();
        }

        start.countDown();
        end.await();

        assertEquals(0, mutex.getViolation(), "Mutex violation이 발생시 상호배제 실패!");
    }
}
