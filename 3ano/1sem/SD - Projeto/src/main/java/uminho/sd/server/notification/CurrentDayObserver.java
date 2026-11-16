package uminho.sd.server.notification;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.*;

import uminho.sd.protocol.SalesEvent;

/**
 * Observes current day events and notifies waiters via Callbacks.
 */
public class CurrentDayObserver {

    private final Thread notificationThread;

    private final Queue<Runnable> taskQueue;
    private final ReentrantLock   queueLock;
    private final Condition       queueNotEmpty;

    private boolean running;

    /* Request Structures */
    private final Map<String, List<SimultaneousRequest>> simultaneousByProduct;
    private final List<ConsecutiveRequest> consecutiveRequests;
    private boolean dayEnded;

    public CurrentDayObserver() {
        this.simultaneousByProduct = new HashMap<>();
        this.consecutiveRequests   = new ArrayList<>();
        this.dayEnded = false;

        this.taskQueue     = new ArrayDeque<>();
        this.queueLock     = new ReentrantLock();
        this.queueNotEmpty = queueLock.newCondition();
        this.running = true;

        this.notificationThread = new Thread(this::processQueue, "Notification-Worker");
        notificationThread.start();
    }

    public void onEvent(SalesEvent event) {
        enqueue(() -> handleEvent(event));
    }

    public void onDayEnd() {
        enqueue(this::handleDayEnd);
    }

    public void reset() {
        enqueue(this::handleReset);
    }

    public Runnable registerSimultaneous(
        String p1, String p2,
        NotificationCallback<Boolean> callback
    ) {
        /* Submit registration task */
        final SimultaneousRequest req = new SimultaneousRequest(p1, p2, callback);
        enqueue(() -> handleRegisterSimultaneous(req));

        /* Return Cancellation Token */
        return () -> enqueue(() -> handleCancelSimultaneous(req));
    }

    public Runnable registerConsecutive(
        int targetCount,
        NotificationCallback<String> callback
    ) {
        /* Submit registration task */
        final ConsecutiveRequest req = new ConsecutiveRequest(targetCount, callback);
        enqueue(() -> handleRegisterConsecutive(req));

        /* Return Cancellation Token */
        return () -> enqueue(() -> handleCancelConsecutive(req));
    }

    /* ---- Event Loop ---- */

    private void processQueue() {
        for( ; ; ) {
            Runnable task;
            queueLock.lock();
            try {
                while (running && taskQueue.isEmpty()) {
                    try {
                        queueNotEmpty.await();
                    } catch (InterruptedException e) {
                        /* Ignore */
                    }
                }

                if (!running && taskQueue.isEmpty()) {
                    return;
                }
                task = taskQueue.poll();
            } finally {
                queueLock.unlock();
            }

            if (task != null) {
                try {
                    task.run();
                } catch (Exception e) {
                    System.err.println("Error in notification task: " + e.getMessage());
                }
            }
        }
    }

    /* ---- Internal Handlers ---- */

    private void handleEvent(SalesEvent event) {
        String product = event.getProduct();

        /* Check Simultaneous Requests */
        List<SimultaneousRequest> simList = simultaneousByProduct.get(product);
        if (simList != null) {
            Iterator<SimultaneousRequest> sit = simList.iterator();

            while (sit.hasNext()) {
                SimultaneousRequest req = sit.next();

                if (req.onProductSold(product)) {
                    /* Completed: Notify result */
                    safeCallback(req.callback, true);
                    sit.remove();
                    removeFromBucket(req, req.otherProduct(product));
                }
            }
        }

        /* Check Consecutive Requests */
        Iterator<ConsecutiveRequest> cit = consecutiveRequests.iterator();
        while (cit.hasNext()) {
            ConsecutiveRequest req = cit.next();

            if (req.onProductSold(product)) {
                /* Completed: Notify result */
                safeCallback(req.callback, req.matchedProduct);
                cit.remove();
            }
        }
    }

    private void handleDayEnd() {
        dayEnded = true;

        /* Fail all simultaneous requests */
        for (List<SimultaneousRequest> list : simultaneousByProduct.values()) {
            for (SimultaneousRequest req : list) {
                if (!req.responded) {
                    safeCallback(req.callback, false);
                    req.responded = true;
                }
            }
        }

        /* Fail all consecutive requests */
        for (ConsecutiveRequest req : consecutiveRequests) {
            safeCallback(req.callback, null);
        }

        simultaneousByProduct.clear();
        consecutiveRequests.clear();
    }

    private void handleReset() {
        dayEnded = false;
    }

    private void handleRegisterSimultaneous(SimultaneousRequest req) {
        if (dayEnded) {
            safeCallback(req.callback, false);
            return;
        }
        simultaneousByProduct.computeIfAbsent(req.p1, k -> new ArrayList<>()).add(req);
        simultaneousByProduct.computeIfAbsent(req.p2, k -> new ArrayList<>()).add(req);
    }

    private void handleCancelSimultaneous(SimultaneousRequest req) {
        removeFromBucket(req, req.p1);
        removeFromBucket(req, req.p2);
    }

    private void handleRegisterConsecutive(ConsecutiveRequest req) {
        if (dayEnded) {
            safeCallback(req.callback, null);
            return;
        }
        consecutiveRequests.add(req);
    }

    private void handleCancelConsecutive(ConsecutiveRequest req) {
        consecutiveRequests.remove(req);
    }

    /* ---- Helpers ---- */

    private void enqueue(Runnable task) {
        queueLock.lock();
        try {
            taskQueue.add(task);
            queueNotEmpty.signal();
        } finally {
            queueLock.unlock();
        }
    }

    private <T> void safeCallback(NotificationCallback<T> cb, T result) {
        try {
            cb.onResult(result);
        } catch (Exception e) {
            System.err.println("Error in callback: " + e.getMessage());
        }
    }

    private void removeFromBucket(SimultaneousRequest req, String product) {
        List<SimultaneousRequest> list = simultaneousByProduct.get(product);
        if (list != null) {
            list.remove(req);

            if (list.isEmpty()) {
                simultaneousByProduct.remove(product);
            }
        }
    }

    public void stop() {
        queueLock.lock();
        try {
            running = false;
            queueNotEmpty.signalAll();
        } finally {
            queueLock.unlock();
        }
        try {
            notificationThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /* ---- Inner Request Classes ---- */

    private static class SimultaneousRequest {

        final String p1, p2;
        final NotificationCallback<Boolean> callback;

        boolean p1Sold    = false;
        boolean p2Sold    = false;
        boolean responded = false;

        SimultaneousRequest(
            String p1, String p2,
            NotificationCallback<Boolean> callback
        ) {
            this.p1 = p1;
            this.p2 = p2;
            this.callback = callback;
        }

        boolean onProductSold(String p) {
            if (p.equals(p1)) {
                p1Sold = true;
            }
            if (p.equals(p2)) {
                p2Sold = true;
            }
            return p1Sold && p2Sold;
        }

        String otherProduct(String p) {
            return p.equals(p1) ? p2 : p1;
        }
    }

    private static class ConsecutiveRequest {

        final int targetCount;
        final NotificationCallback<String> callback;

        String lastProduct    = null;
        int    currentCount   = 0;
        String matchedProduct = null;

        ConsecutiveRequest(
            int targetCount,
            NotificationCallback<String> callback
        ) {
            this.targetCount = targetCount;
            this.callback    = callback;
        }

        boolean onProductSold(String p) {
            if (p.equals(lastProduct)) {
                currentCount++;
            } else {
                lastProduct  = p;
                currentCount = 1;
            }

            if (currentCount == targetCount) {
                matchedProduct = p;
                return true;
            }
            return false;
        }
    }
}
