package uminho.sd.server.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.Iterator;
import java.util.stream.Stream;

import uminho.sd.protocol.SalesEvent;

/**
 * Lazy aggregation cache for product statistics.
 */
public class AggregationCache {

    private final Map<Integer, Map<String, ProductStats>> cache;
    private final ReentrantReadWriteLock lock;
    private final int maxDays;

    public AggregationCache(int maxDays) {
        this.cache   = new HashMap<>();
        this.lock    = new ReentrantReadWriteLock();
        this.maxDays = maxDays;
    }

    public ProductStats getOrCompute(
        int dayNumber, String product, Day day,
        Function<Integer, Stream<SalesEvent>> eventStreamer
    ) {
        ProductStats stats;

        lock.readLock().lock();
        try {
            Map<String, ProductStats> dayCache = cache.get(dayNumber);
            if (dayCache != null) {
                stats = dayCache.get(product);
                if (stats != null) {
                    return stats;
                }
                return new ProductStats(
                    product, 0, 0.0,
                    0.0, 0.0
                );
            }
        } finally {
            lock.readLock().unlock();
        }

        /*
         * Cache miss for the entire day.
         * Compute ALL products for this day (outside lock).
         */
        Map<String, ProductStats> computedMap = computeAllStats(
            dayNumber, eventStreamer
        );

        /* Write Lock to update cache */
        lock.writeLock().lock();
        try {
            Map<String, ProductStats> dayCache = cache.get(dayNumber);

            if (dayCache == null) {
                cache.put(dayNumber, computedMap);
                enforceDayCap(dayNumber);
                dayCache = computedMap;
            }

            stats = dayCache.get(product);
            return stats != null ? stats : new ProductStats(
                product, 0, 0.0,
                0.0, 0.0
            );
        } finally {
            lock.writeLock().unlock();
        }
    }

    private Map<String, ProductStats> computeAllStats(
        int dayNumber, Function<Integer, Stream<SalesEvent>> eventStreamer
    ) {
        Map<String, Aggregator> aggregators = new HashMap<>();

        try (Stream<SalesEvent> stream = eventStreamer.apply(dayNumber)) {
            Iterator<SalesEvent> it = stream.iterator();
            while (it.hasNext()) {
                SalesEvent event = it.next();
                aggregators.computeIfAbsent(
                    event.getProduct(), Aggregator::new
                ).add(event);
            }
        } catch (Exception e) {
            System.err.println(
                "Warning: Error reading day " +
                dayNumber + ": " + e.getMessage()
            );
        }

        /* Convert to ProductStats */
        Map<String, ProductStats> results = new HashMap<>();
        for (Map.Entry<String, Aggregator> entry : aggregators.entrySet()) {
            results.put(entry.getKey(), entry.getValue().toStats());
        }
        return results;
    }

    /* Helper for calculating stats */
    private static class Aggregator {

        final String product;
        int    qty      = 0;
        double volume   = 0.0;
        double maxPrice = 0.0;

        Aggregator(String product) {
            this.product = product;
        }

        void add(SalesEvent e) {
            qty     += e.getQuantity();
            volume  += e.getQuantity() * e.getPrice();
            maxPrice = Math.max(maxPrice, e.getPrice());
        }

        ProductStats toStats() {
            double avg = qty > 0 ? volume / qty : 0.0;
            return new ProductStats(product, qty, volume, avg, maxPrice);
        }
    }

    public void evictOlderThan(int oldestRelevantDay) {
        lock.writeLock().lock();
        try {
            cache.keySet().removeIf(day -> day < oldestRelevantDay);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void enforceDayCap(int justAddedDay) {
        if (cache.size() <= maxDays) {
            return;
        }

        int oldest = Integer.MAX_VALUE;
        for (Integer k : cache.keySet()) {
            if (k != justAddedDay && k < oldest) {
                oldest = k;
            }
        }
        if (oldest != Integer.MAX_VALUE) {
            cache.remove(oldest);
        }
    }
}
