package sync;

import java.util.UUID;
import java.util.concurrent.Semaphore;

/**
 * 예약 동시 처리량 제어를 위한 세마포어 예제
 */
public class SemaphoreForReservation {
    private final int MAX_AVAILABLE = 5;
    private final Semaphore pageAccessAvailable = new Semaphore(MAX_AVAILABLE);

    private final String[] pageAccessKeys = new String[MAX_AVAILABLE];

    public String getPageAccessKey() throws InterruptedException {
        pageAccessAvailable.acquire();
        try {
            return getAvailableAccessKey();
        } catch (Exception e) {
            pageAccessAvailable.release();
            throw new RuntimeException(e);
        }
    }

    public void releasePageAccessKey(String pageAccessKey) {
        if (releaseUsedAccessKey(pageAccessKey)) {
            pageAccessAvailable.release();
        }
    }

    private synchronized boolean releaseUsedAccessKey(String pageAccessKey) {
        if (pageAccessKey == null) {
            throw new IllegalArgumentException("pageAccessKey is null");
        }

        for (int i = 0; i < MAX_AVAILABLE; i++) {
            if (pageAccessKey.equals(pageAccessKeys[i])) {
                pageAccessKeys[i] = null;
                return true;
            }
        }
        return false;
    }

    private synchronized String getAvailableAccessKey() {
        for (int i = 0; i < MAX_AVAILABLE; i++) {
            if (pageAccessKeys[i] == null) {
                pageAccessKeys[i] = UUID.randomUUID().toString();
                return pageAccessKeys[i];
            }
        }
        return null;
    }
}
