package uminho.sd.server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import java.util.concurrent.locks.*;

import uminho.sd.client.SalesClient;
import uminho.sd.server.core.Server;

/**
 * Helper utilities for server integration/performance tests.
 * Manages lifecycle and common client operations.
 */
public class ServerTestHelper {

    private static final int TEST_PORT                 = 50002;
    private static final int TEST_MAX_DAYS             = 30;
    private static final int TEST_MAX_SERIES_IN_MEMORY = 10;

    private Server server;
    private Thread serverThread;

    private final ReentrantLock lock;
    private final Condition     serverCondition;

    private boolean serverReady;

    public ServerTestHelper() {
        this.lock            = new ReentrantLock();
        this.serverCondition = lock.newCondition();

        this.serverReady = false;
    }

    /*
     * Start test server with default tunables.
     */
    public void startServer() throws InterruptedException {
        startServer(TEST_MAX_DAYS, TEST_MAX_SERIES_IN_MEMORY);
    }

    /*
     * Start test server with custom D and S.
     */
    public void startServer(int maxDays, int maxSeriesInMemory) throws InterruptedException {
        cleanDataDir();
        try {
            server = new Server(TEST_PORT, maxDays, maxSeriesInMemory);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize server", e);
        }

        serverThread = new Thread(() -> {
            try {
                server.start(() -> {
                    try {
                        /* Allow socket to fully bind */
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        /* Ignore */
                    }
                    lock.lock();
                    try {
                        serverReady = true;
                        serverCondition.signalAll();
                    } finally {
                        lock.unlock();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        lock.lock();
        try {
            while (!serverReady) {
                serverCondition.await();
            }
        } finally {
            lock.unlock();
        }
    }

    /*
     * Stop test server.
     */
    public void stopServer() throws IOException, InterruptedException {
        if (server != null) {
            server.stop();
        }

        if (serverThread != null) {
            serverThread.interrupt();
            serverThread.join(2000);
        }

        lock.lock();
        try {
            serverReady = false;
        } finally {
            lock.unlock();
        }
    }

    public int getPort() {
        return TEST_PORT;
    }

    /* ---- Convenience operations for tests ---- */

    private SalesClient newClient() throws IOException {
        return new SalesClient("localhost", TEST_PORT);
    }

    public void ensureUser(String username, String password) throws Exception {
        try (SalesClient client = newClient()) {
            try {
                client.register(username, password);
            } catch (Exception ignored) {
                /* already exists */
            }
        }
    }

    public SalesClient newLoggedClient(String username, String password)
            throws Exception {
        SalesClient client = newClient();
        client.login(username, password);
        return client;
    }

    public void resetDay(String username, String password) throws Exception {
        try (SalesClient client = newLoggedClient(username, password)) {
            client.startNewDay();
        }
        Thread.sleep(50);
    }

    /* ---- Concurrency & Load Testing Utilities ---- */

    public interface ClientTask {
        /* Task logic. Can report multiple successes/errors to metrics if it runs a loop. */
        void run(SalesClient client, int clientIndex, TestMetrics metrics) throws Exception;
    }

    public static class TestMetrics {

        private int successCount = 0;
        private int errorCount   = 0;

        private final List<Long>      latencies  = new ArrayList<>();
        private final List<Exception> exceptions = new ArrayList<>();

        private final ReentrantLock lock = new ReentrantLock();

        void recordSuccess(long latencyNs) {
            lock.lock();
            try {
                successCount++;
                latencies.add(latencyNs);
            } finally {
                lock.unlock();
            }
        }

        void recordSuccesses(int count) {
            lock.lock();
            try {
                successCount += count;
            } finally {
                lock.unlock();
            }
        }

        void recordError(Exception e) {
            lock.lock();
            try {
                errorCount++;
                exceptions.add(e);
            } finally {
                lock.unlock();
            }
        }

        public void printSummary(String label, double totalDurationMs) {
            lock.lock();
            try {
                System.out.println("\n─── " + label + " ───");
                System.out.println("Successful Ops: " + successCount);
                System.out.println("Failed Ops:     " + errorCount);
                System.out.println("Total Time:     " + String.format("%.2f", totalDurationMs) + " ms");

                double throughput = 0;
                if (totalDurationMs > 0) {
                    throughput = (successCount / totalDurationMs) * 1000;
                }
                System.out.println("Throughput:     " + String.format("%.0f", throughput) + " ops/sec");

                if (!latencies.isEmpty()) {
                    latencies.sort(Long::compareTo);
                    double p50 = latencies.get((int) (latencies.size() * 0.50)) / 1_000_000.0;
                    double p95 = latencies.get((int) (latencies.size() * 0.95)) / 1_000_000.0;
                    double p99 = latencies.get((int) (latencies.size() * 0.99)) / 1_000_000.0;
                    double max = latencies.get(latencies.size() - 1) / 1_000_000.0;

                    System.out.println("Latency (ms):  "     +
                        " P50=" + String.format("%.2f", p50) +
                        " P95=" + String.format("%.2f", p95) +
                        " P99=" + String.format("%.2f", p99) +
                        " Max=" + String.format("%.2f", max) + "\n"
                    );
                }

                if (!exceptions.isEmpty()) {
                    System.out.println("First Exception: " + exceptions.get(0).getMessage());
                }
            } finally {
                lock.unlock();
            }
        }

        public void assertSuccess(int expectedSuccess) {
            lock.lock();
            try {
                if (errorCount > 0) {
                    throw new AssertionError(
                        "Test errors occurred! First error: " +
                        (exceptions.isEmpty() ? "Unknown" : exceptions.get(0).getMessage())
                    );
                }

                if (successCount != expectedSuccess) {
                    throw new AssertionError(
                        "Expected " + expectedSuccess + " successes but got " + successCount
                    );
                }
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Simulates multiple users opening multiple connections.
     */
    public TestMetrics runConcurrentClients(
        int numClients, String username, String password, ClientTask task
    ) throws InterruptedException {
        return runConcurrentClients(numClients, (i) -> {
            try {
                return newLoggedClient(username, password);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, task);
    }

    /**
     * Core concurrent execution method.
     */
    public TestMetrics runConcurrentClients(
        int numClients, Function<Integer, SalesClient> clientFactory, ClientTask task
    ) throws InterruptedException {
        Thread[]    threads = new Thread[numClients];
        TestMetrics metrics = new TestMetrics();

        ReentrantLock startLock  = new ReentrantLock();
        Condition startCondition = startLock.newCondition();

        final boolean[] started = { false };

        for (int i = 0; i < numClients; i++) {
            final int index = i;

            threads[i] = new Thread(() -> {
                startLock.lock();
                try {
                    while (!started[0]) {
                        startCondition.await();
                    }
                } catch (InterruptedException e) {
                    return;
                } finally {
                    startLock.unlock();
                }

                /* Run Task */
                try (SalesClient client = clientFactory.apply(index)) {
                    try {
                        /* Task is responsible for recording metrics */
                        task.run(client, index, metrics);
                    } catch (Exception e) {
                        metrics.recordError(e);
                    }
                } catch (Exception e) {
                    metrics.recordError(e);
                }
            });
            threads[i].start();
        }

        Thread.sleep(200); long wallStart = System.nanoTime();

        startLock.lock();
        try {
            started[0] = true;
            startCondition.signalAll();
        } finally {
            startLock.unlock();
        }

        for (Thread t : threads) {
            t.join();
        }

        long wallEnd      = System.nanoTime();
        double durationMs = (wallEnd - wallStart) / 1_000_000.0;

        metrics.printSummary(numClients + " Clients", durationMs);

        return metrics;
    }

    private void cleanDataDir() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            return;
        }
        deleteRecursive(dataDir);
    }

    private void deleteRecursive(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursive(child);
                }
            }
        }
        file.delete();
    }
}
