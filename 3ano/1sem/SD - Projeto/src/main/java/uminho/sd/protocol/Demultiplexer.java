package uminho.sd.protocol;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Demultiplexer manages concurrent request/response handling for multi-threaded clients.
 * A background receiver thread continuously reads frames and routes them to waiting threads.
 */
public class Demultiplexer implements AutoCloseable {

    private final TaggedConnection connection;
    private final Map<Integer, FrameQueue> pending;
    private final ReentrantLock lock;
    private final Thread receiverThread;

    private int     nextTag;
    private boolean closed;

    private Exception receiverException;

    /**
     * Creates a Demultiplexer and starts the receiver thread.
     *
     * @param connection The underlying connection
     */
    public Demultiplexer(TaggedConnection connection) {
        this.connection = connection;
        this.pending = new HashMap<>();
        this.lock = new ReentrantLock();

        this.nextTag = 1;
        this.closed  = false;

        this.receiverException = null;

        /* Start receiver thread as daemon so it doesn't block JVM shutdown */
        this.receiverThread = new Thread(this::receiverLoop, "Demux-Receiver");
        this.receiverThread.setDaemon(true);
        this.receiverThread.start();
    }

    /**
     * Sends a request and waits for the response.
     * Thread-safe: multiple threads can call this concurrently.
     *
     * @param requestType Type of request
     * @param payload     Request payload
     * @return Response frame
     */
    public Frame send(byte requestType, byte[] payload) throws IOException, InterruptedException {
        int tag;
        FrameQueue queue = new FrameQueue();

        lock.lock();
        try {
            if (closed) {
                throw new IOException("Demultiplexer is closed");
            }
            if (receiverException != null) {
                throw new IOException("Receiver thread failed", receiverException);
            }

            tag = nextTag;
            if (nextTag == Integer.MAX_VALUE) {
                nextTag = 1;  /* overflow */
            } else {
                nextTag++;
            }

            pending.put(tag, queue);
        } finally {
            lock.unlock();
        }

        Frame request = new Frame(tag, requestType, payload);
        try {
            connection.send(request);
        } catch (IOException e) {
            lock.lock();
            try {
                pending.remove(tag);
            } finally {
                lock.unlock();
            }
            throw e;
        }

        return queue.getFrame();
    }

    /**
     * Background receiver thread loop
     */
    private void receiverLoop() {
        try {
            for( ; ; ) {
                /* Receive a frame (blocking) - will throw IOException when closed */
                Frame frame;
                try {
                    frame = connection.receive();
                } catch (IOException e) {
                    /* Connection closed or error - notify all waiting threads */
                    lock.lock();
                    try {
                        receiverException = e;
                        for (FrameQueue queue : pending.values()) {
                            queue.setException(e);
                        }
                        pending.clear();
                    } finally {
                        lock.unlock();
                    }
                    break;
                }

                /* Route frame to the correct waiting thread */
                lock.lock();
                try {
                    FrameQueue queue = pending.remove(frame.getTag());
                    if (queue != null) {
                        queue.putFrame(frame);
                    }
                    /* If queue is null, it's an unexpected response - ignore it */
                } finally {
                    lock.unlock();
                }
            }
        } catch (Exception e) {
            /* Unexpected error in receiver thread */
            lock.lock();
            try {
                receiverException = e;
                for (FrameQueue queue : pending.values()) {
                    queue.setException(e);
                }
                pending.clear();
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Closes the demultiplexer and stops the receiver thread
     */
    @Override
    public void close() throws IOException {
        lock.lock();
        try {
            if (closed) {
                return;
            }
            closed = true;

            /* Wake up all waiting threads */
            for (FrameQueue queue : pending.values()) {
                queue.setException(new IOException("Demultiplexer closed"));
            }
            pending.clear();
        } finally {
            lock.unlock();
        }

        /* Close the connection (will cause receiver to exit) */
        connection.close();

        /* Wait for receiver thread to finish */
        try {
            receiverThread.join(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Internal class representing a queue for a single request/response.
     * Each client thread waiting for a response has its own FrameQueue.
     */
    private static class FrameQueue {

        private Frame     frame     = null;
        private Exception exception = null;

        private final ReentrantLock lock         = new ReentrantLock();
        private final Condition     frameArrived = lock.newCondition();

        /**
         * Called by receiver thread to deliver the response frame.
         */
        void putFrame(Frame f) {
            lock.lock();
            try {
                this.frame = f;
                frameArrived.signal();
            } finally {
                lock.unlock();
            }
        }

        /**
         * Called by receiver thread to signal an error (e.g., connection lost).
         */
        void setException(Exception e) {
            lock.lock();
            try {
                this.exception = e;
                frameArrived.signal();
            } finally {
                lock.unlock();
            }
        }

        /**
         * Blocks until the response frame arrives or an error occurs.
         *
         * @return The response Frame
         */
        Frame getFrame() throws IOException, InterruptedException {
            lock.lock();
            try {
                while (frame == null && exception == null) {
                    frameArrived.await();
                }

                if (exception != null) {
                    if (exception instanceof IOException) {
                        throw (IOException) exception;
                    } else {
                        throw new IOException("Error receiving response", exception);
                    }
                }

                return frame;
            } finally {
                lock.unlock();
            }
        }
    }
}
