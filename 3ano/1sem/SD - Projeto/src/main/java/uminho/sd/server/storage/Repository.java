package uminho.sd.server.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.Spliterators;
import java.util.Spliterator;

import uminho.sd.protocol.SalesEvent;
import uminho.sd.server.notification.CurrentDayObserver;

/**
 * The Repository manages all data access, persistence, and caching.
 */
public class Repository implements AutoCloseable {

    private final File storageDir;
    private final int maxDays;

    private final ReentrantReadWriteLock lock;

    private Day currentDay;
    private EventWriter eventWriter;

    private final DayCache dayCache;
    private final AggregationCache aggregationCache;

    private final CurrentDayObserver dayObserver;

    private static final String DAY_PREFIX = "day_";
    private static final String DAY_SUFFIX = ".sales";

    public Repository(int maxDays, int maxSeriesInMemory) throws IOException {
        this.storageDir = new File("./data");
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                throw new IOException(
                    "Failed to create storage directory: " + storageDir
                );
            }
        }

        this.maxDays = maxDays;
        this.lock = new ReentrantReadWriteLock();

        this.dayObserver = new CurrentDayObserver();

        this.dayCache         = new DayCache(maxSeriesInMemory, this::loadDayMetadata);
        this.aggregationCache = new AggregationCache(maxDays);

        /* Clean up existing data and start from Day 1 */
        cleanStorageDirectory();

        int currentDayNum = 1;
        this.currentDay   = new Day(currentDayNum);
        this.eventWriter  = new EventWriter(getDayFile(currentDayNum));

        System.out.println(
            "Repository initialized: D=" + maxDays +
            ", S=" + maxSeriesInMemory
        );
    }

    private void cleanStorageDirectory() {
        File[] files = storageDir.listFiles(
            (dir, name) -> name.startsWith(DAY_PREFIX)
                        && name.endsWith(DAY_SUFFIX)
        );

        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

    /* ---- Write Operations ---- */

    public void addEvent(String product, int quantity, double price) {
        SalesEvent event = new SalesEvent(product, quantity, price);
        eventWriter.submit(event);

        lock.readLock().lock();
        try {
            currentDay.incrementEventCount();
        } finally {
            lock.readLock().unlock();
        }

        dayObserver.onEvent(event);
    }

    public void startNewDay() {
        int frozenDayNum;
        int oldestDay;

        Day dayToFreeze;
        EventWriter writerToStop;

        lock.writeLock().lock();
        try {
            dayToFreeze  = currentDay;
            writerToStop = eventWriter;

            frozenDayNum  = currentDay.getDayNumber();
            int newDayNum = frozenDayNum + 1;

            currentDay = new Day(newDayNum);
            try {
                this.eventWriter = new EventWriter(getDayFile(newDayNum));
            } catch (IOException e) {
                throw new RuntimeException("CRITICAL: Failed to rotate day log", e);
            }

            dayObserver.onDayEnd();
            dayObserver.reset();

            System.out.println("Started new day: " + newDayNum);

        } finally {
            lock.writeLock().unlock();
        }

        /* Expensive Cleanup done outside lock */

        /* Freeze and cache the old day */
        dayToFreeze.freeze();
        System.out.println(
            "Day " + frozenDayNum + " frozen (" +
            dayToFreeze.getEventCount() + " events)"
        );
        dayCache.putDay(dayToFreeze);

        if (writerToStop != null) {
            writerToStop.stop();
        }

        /* Evict Old Data */
        oldestDay = frozenDayNum - maxDays + 1;
        aggregationCache.evictOlderThan(oldestDay);
        deleteDaysOlderThan(oldestDay);
    }

    /* ---- Read Operations ---- */

    public int getSalesQuantity(String product, int days) {
        int currentNum = getCurrentDayNumber();
        int startDay   = Math.max(0, currentNum - days);

        int total = 0;
        for (int d = startDay; d < currentNum; d++) {
            Day day = getDay(d);
            if (day != null) {
                ProductStats s = aggregationCache.getOrCompute(
                    d, product, day, this::streamEvents
                );
                total += s.getQuantity();
            }
        }
        return total;
    }

    public double getSalesVolume(String product, int days) {
        int currentNum = getCurrentDayNumber();
        int startDay   = Math.max(0, currentNum - days);

        double total = 0.0;
        for (int d = startDay; d < currentNum; d++) {
            Day day = getDay(d);
            if (day != null) {
                ProductStats s = aggregationCache.getOrCompute(
                    d, product, day, this::streamEvents
                );
                total += s.getVolume();
            }
        }
        return total;
    }

    public double getAvgPrice(String product, int days) {
        int currentNum = getCurrentDayNumber();
        int startDay   = Math.max(0, currentNum - days);

        double volume = 0.0;
        int    qty    = 0;
        for (int d = startDay; d < currentNum; d++) {
            Day day = getDay(d);
            if (day != null) {
                ProductStats s = aggregationCache.getOrCompute(
                    d, product, day, this::streamEvents
                );
                volume += s.getVolume();
                qty += s.getQuantity();
            }
        }
        return qty > 0 ? volume / qty : 0.0;
    }

    public double getMaxPrice(String product, int days) {
        int currentNum = getCurrentDayNumber();
        int startDay = Math.max(0, currentNum - days);

        double max = 0.0;
        for (int d = startDay; d < currentNum; d++) {
            Day day = getDay(d);
            if (day != null) {
                ProductStats s = aggregationCache.getOrCompute(
                    d, product, day, this::streamEvents
                );
                max = Math.max(max, s.getMaxPrice());
            }
        }
        return max;
    }

    public List<SalesEvent> filterEvents(Set<String> products, int dayOffset) {
        int targetDay = getCurrentDayNumber() - dayOffset;
        if (targetDay < 0) {
            return new ArrayList<>();
        }

        Day day = getDay(targetDay);
        if (day == null) {
            return new ArrayList<>();
        }

        try (Stream<SalesEvent> stream = streamEvents(targetDay)) {
            return stream.filter(
                e -> products.contains(e.getProduct())
            ).collect(Collectors.toList());
        }
    }

    /* ---- Internal / Helpers ---- */

    public int getCurrentDayNumber() {
        lock.readLock().lock();
        try {
            return currentDay.getDayNumber();
        } finally {
            lock.readLock().unlock();
        }
    }

    private Day getDay(int dayNumber) {
        return dayCache.getDay(dayNumber);
    }

    private Day loadDayMetadata(int dayNumber) {
        File f = getDayFile(dayNumber);
        if (!f.exists()) {
            return null;
        }

        Day day = new Day(dayNumber);
        day.setEventCount(-1);
        day.freeze();
        return day;
    }

    /* Streamer for AggregationCache */
    public Stream<SalesEvent> streamEvents(int dayNumber) {
        File f = getDayFile(dayNumber);
        if (!f.exists()) {
            return Stream.empty();
        }

        try {
            EventReader eventReader = new EventReader(f);

            /*
             * Wraps eventReader in a sequential Stream with ORDERED elements,
             * automatically closing it when the stream is closed
             */
            return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(eventReader, Spliterator.ORDERED),
                false
            ).onClose(eventReader::close);
        } catch (IOException e) {
            return Stream.empty();
        }
    }

    private void deleteDaysOlderThan(int dayNum) {
        for (int i = 0; i < dayNum; i++) {
            File f = getDayFile(i);
            if (f.exists()) {
                f.delete();
            }
        }
    }

    private File getDayFile(int dayNum) {
        return new File(storageDir, DAY_PREFIX + dayNum + DAY_SUFFIX);
    }

    public CurrentDayObserver getObserver() {
        return dayObserver;
    }

    @Override
    public void close() {
        if (eventWriter != null) {
            eventWriter.stop();
        }
        dayObserver.stop();
    }
}
