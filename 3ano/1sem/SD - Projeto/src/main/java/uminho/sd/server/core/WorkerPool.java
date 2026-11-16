package uminho.sd.server.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simple bounded worker pool.
 * Accepts Runnable tasks. Blocks submitter when the queue is full.
 */
public class WorkerPool {

    private final ReentrantLock lock     = new ReentrantLock();
    private final Condition     notEmpty = lock.newCondition();
    private final Condition     notFull  = lock.newCondition();

    private final ArrayDeque<Runnable> queue   = new ArrayDeque<>();
    private final ArrayList<Thread>    workers = new ArrayList<>();

    private final int     capacity;
    private       boolean running;

    public WorkerPool(int workerCount, int capacity) {
        this.capacity = capacity;
        this.running  = false;

        for (int i = 0; i < workerCount; i++) {
            Thread t = new Thread(this::workerLoop, "Worker-" + i);
            t.setDaemon(true);
            workers.add(t);
        }
    }

    public void start() {
        lock.lock();
        try {
            if (running) {
                return;
            }
            running = true;
        } finally {
            lock.unlock();
        }

        for (Thread t : workers) {
            t.start();
        }
    }

    public void stop() {
        lock.lock();
        try {
            if (!running) {
                return;
            }

            running = false;
            notEmpty.signalAll();
            notFull.signalAll();
        } finally {
            lock.unlock();
        }

        for (Thread t : workers) {
            try {
                t.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Submit a task; blocks if the queue is full.
     */
    public void submit(Runnable task) throws InterruptedException {
        lock.lock();
        try {
            while (running && queue.size() >= capacity) {
                notFull.await();
            }

            if (!running) {
                throw new IllegalStateException("WorkerPool stopped");
            }

            queue.addLast(task);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    private void workerLoop() {
        for( ; ; ) {
            Runnable task = null;
            lock.lock();
            try {
                while (running && queue.isEmpty()) {
                    notEmpty.await();
                }

                if (!running && queue.isEmpty()) {
                    return;
                }

                task = queue.removeFirst();
                notFull.signal();
            } catch (InterruptedException e) {
                if (!running) {
                    lock.unlock();
                    return;
                }
            } finally {
                lock.unlock();
            }

            if (task != null) {
                try {
                    task.run();
                } catch (Throwable ignored) {
                    /* Swallow task exceptions to keep worker alive */
                }
            }
        }
    }
}
