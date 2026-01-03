package sync;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SemaphoreForReservationTest {
    @Test
    @DisplayName("예약 처리는 동시에 최대 5개까지 처리 가능하다")
    public void test() throws InterruptedException {
        int threadCount = 20;
        SemaphoreForReservation sem = new SemaphoreForReservation();
        AtomicInteger concurrentCount = new AtomicInteger(0);
        AtomicInteger maxConcurrent = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            Thread worker = new Thread(() -> {
                String accessKey = null;
                try {
                    accessKey = sem.getPageAccessKey();
                    if (accessKey != null) {
                        int currentCount = concurrentCount.incrementAndGet();
                        maxConcurrent.updateAndGet(max -> Math.max(max, currentCount));

                        // 예약 처리
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    concurrentCount.decrementAndGet();
                    if (accessKey != null) {
                        sem.releasePageAccessKey(accessKey);
                    }
                    latch.countDown();
                }
            }, "thread-worker-" + i);
            worker.start();
        }

        latch.await();

        assertTrue(maxConcurrent.get() <= 5,
            "동시 접근 수가 5를 초과했습니다: " + maxConcurrent.get());
    }
}
