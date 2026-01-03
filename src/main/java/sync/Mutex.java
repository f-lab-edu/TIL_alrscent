package sync;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Mutex 예제
 */
public class Mutex {
    private final ReentrantLock lock = new ReentrantLock(true);
    private final AtomicInteger inThreadCount = new AtomicInteger(0);
    private final AtomicInteger violation = new AtomicInteger(0);

    public void doSomething() {
        lock.lock();
        try {
            inThreadCount.incrementAndGet();
            if (inThreadCount.get() > 1) {
                violation.incrementAndGet();
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } finally {
            inThreadCount.decrementAndGet();
            lock.unlock();
        }
    }

    public int getViolation() {
        return violation.get();
    }
}
