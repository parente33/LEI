package uminho.sd.server.storage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * LRU cache for days in memory. Maximum S days in memory at once.
 */
public class DayCache {

    private final int maxDaysInMemory;
    private final ReentrantLock lock;
    private final Function<Integer, Day> loader;

    /* LRU cache: dayNumber - Day */
    private final LinkedHashMap<Integer, Day> cache;

    public DayCache(int maxDaysInMemory_, Function<Integer, Day> loader) {
        this.maxDaysInMemory = maxDaysInMemory_;
        this.loader = loader;
        this.lock = new ReentrantLock();

        this.cache = new LinkedHashMap<Integer, Day>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Day> eldest) {
                if (size() > maxDaysInMemory) {
                    return true;
                }
                return false;
            }
        };
    }

    public Day getDay(int dayNumber) {
        Day day;

        lock.lock();
        try {
            day = cache.get(dayNumber);
            if (day != null) {
                return day;
            }
        } finally {
            lock.unlock();
        }

        /* Cache miss - load from disk OUTSIDE lock */
        day = loader.apply(dayNumber);

        if (day != null) {
            lock.lock();
            try {
                /* Double check in case another thread loaded it */
                Day existing = cache.get(dayNumber);
                if (existing != null) {
                    return existing;
                }
                cache.put(dayNumber, day);
            } finally {
                lock.unlock();
            }
        }

        return day;
    }

    public void putDay(Day day) {
        lock.lock();
        try {
            cache.put(day.getDayNumber(), day);
        } finally {
            lock.unlock();
        }
    }
}
