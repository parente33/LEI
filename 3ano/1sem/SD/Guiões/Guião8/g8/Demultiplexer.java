package g8;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.*;
import g8.TaggedConnection.Frame;

public class Demultiplexer implements AutoCloseable {

    private final TaggedConnection conn;

    // Por tag: (lock, condition, queue)
    private final Map<Integer, Entry> map = new HashMap<>();

    // Criação/lookup de entries
    private final ReentrantLock global = new ReentrantLock();

    private volatile boolean closed = false;

    private static class Entry {
        final ReentrantLock lock = new ReentrantLock();
        final Condition hasData = lock.newCondition();
        final Deque<byte[]> queue = new ArrayDeque<>();
    }

    public Demultiplexer(TaggedConnection conn) {
        this.conn = conn;
    }

    public void start() {
        Thread reader = new Thread(this::readerLoop);
        reader.start();
    }

    private Entry getEntry(int tag) {
        global.lock();
        try {
            return map.computeIfAbsent(tag, t -> new Entry());
        } finally {
            global.unlock();
        }
    }

    private void readerLoop() {
        try {
            for (;;) {
                Frame f = conn.receive();

                Entry e = getEntry(f.tag);
                e.lock.lock();
                try {
                    e.queue.addLast(f.data);
                    e.hasData.signal();
                } finally {
                    e.lock.unlock();
                }
            }
        } catch (Exception ignored) {
            // encerrou
        } finally {
            closed = true;
            // despertar todas as condições
            global.lock();
            try {
                for (Entry e : map.values()) {
                    e.lock.lock();
                    try { e.hasData.signalAll(); }
                    finally { e.lock.unlock(); }
                }
            } finally {
                global.unlock();
            }
        }
    }

    public void send(Frame frame) throws IOException {
        conn.send(frame);
    }

    public void send(int tag, byte[] data) throws IOException {
        conn.send(tag, data);
    }

    public byte[] receive(int tag) throws IOException, InterruptedException {
        Entry e = getEntry(tag);
        e.lock.lock();
        try {
            for (;;) {
                if (!e.queue.isEmpty())
                    return e.queue.removeFirst();
                if (closed)
                    throw new IOException("Connection closed");
                e.hasData.await();
            }
        } finally {
            e.lock.unlock();
        }
    }

    @Override
    public void close() throws IOException {
        closed = true;
        conn.close();
    }
}