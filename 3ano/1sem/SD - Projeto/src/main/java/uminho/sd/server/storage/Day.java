package uminho.sd.server.storage;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a single day's metadata.
 * Events are stored on disk and accessed via Repository.
 */
public class Day {

    private final int dayNumber;

    private int     eventCount;
    private boolean frozen;

    private final ReentrantLock lock;

    public Day(int dayNumber) {
        this.dayNumber = dayNumber;

        this.eventCount = 0;
        this.frozen     = false;

        this.lock = new ReentrantLock();
    }

    public void incrementEventCount() {
        lock.lock();
        try {
            if (frozen) {
                throw new IllegalStateException("Cannot modify frozen day");
            }
            eventCount++;
        } finally {
            lock.unlock();
        }
    }

    public void setEventCount(int count) {
        lock.lock();
        try {
            if (frozen) {
                throw new IllegalStateException("Cannot modify frozen day");
            }
            this.eventCount = count;
        } finally {
            lock.unlock();
        }
    }

    public void freeze() {
        lock.lock();
        try {
            frozen = true;
        } finally {
            lock.unlock();
        }
    }

    public int getEventCount() {
        lock.lock();
        try {
            return eventCount;
        } finally {
            lock.unlock();
        }
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public boolean isFrozen() {
        lock.lock();
        try {
            return frozen;
        } finally {
            lock.unlock();
        }
    }
}
