package uminho.sd.server.storage;

import java.io.*;
import java.util.ArrayDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import uminho.sd.protocol.SalesEvent;

/**
 * Writes SalesEvents to a file using DataOutputStream.
 * Uses a thread-safe queue and a dedicated writer thread
 * to perform concurrent, batched file writes.
 */
public class EventWriter {

    private static final int BATCH_SIZE = 16384;

    private final ReentrantLock lock;
    private final Condition     notEmpty;

    private ArrayDeque<SalesEvent> queue;
    private final Thread writerThread;

    private boolean running;
    private final DataOutputStream out;

    public EventWriter(File file) throws IOException {
        this.lock     = new ReentrantLock();
        this.notEmpty = lock.newCondition();

        this.queue   = new ArrayDeque<>();
        this.running = true;

        FileOutputStream fos = new FileOutputStream(file, true);
        this.out = new DataOutputStream(new BufferedOutputStream(fos, 1024 * 1024));

        this.writerThread = new Thread(this::writerLoop, "EventWriter-Writer");
        this.writerThread.start();
    }

    public void submit(SalesEvent event) {
        lock.lock();
        try {
            queue.addLast(event);
            /* Only signal if we transitioned from empty */
            if (queue.size() == 1) {
                notEmpty.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    private void writerLoop() {
        ArrayDeque<SalesEvent> batchBuffer = new ArrayDeque<>(BATCH_SIZE);

        try {
            for (;;) {
                lock.lock();
                try {
                    while (running && queue.isEmpty()) {
                        notEmpty.await();
                    }

                    if (!running && queue.isEmpty()) {
                        break;
                    }

                    /* Double Buffering: Swap current queue with the spare batchBuffer */
                    ArrayDeque<SalesEvent> temp = queue;
                    queue       = batchBuffer;
                    batchBuffer = temp;

                    /* Now batchBuffer holds the data, and queue is empty/ready */

                } finally {
                    lock.unlock();
                }

                /* Write the swapped buffer outside the lock */
                if (!batchBuffer.isEmpty()) {
                    for (SalesEvent event : batchBuffer) {
                        out.writeUTF(event.getProduct());
                        out.writeInt(event.getQuantity());
                        out.writeDouble(event.getPrice());
                    }
                    out.flush();
                    batchBuffer.clear();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            System.err.println("CRITICAL: EventWriter failed to write batch: " + e.getMessage());
        } finally {
            closeStream();
        }
    }

    private void closeStream() {
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("Error closing EventWriter: " + e.getMessage());
        }
    }

    public void stop() {
        lock.lock();
        try {
            running = false;
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }

        try {
            writerThread.join(5000);
            if (writerThread.isAlive()) {
                System.err.println(
                    "Warning: EventWriter thread did not stop in time."
                );
                writerThread.interrupt();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
