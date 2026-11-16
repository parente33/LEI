package uminho.sd.server.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import uminho.sd.protocol.TaggedConnection;
import uminho.sd.server.auth.AuthService;
import uminho.sd.server.net.ClientHandler;
import uminho.sd.server.storage.Repository;

/**
 * Main server class managing connections and shared services.
 */
public class Server {

    private final int port;
    private final int maxDays;
    private final int maxSeriesInMemory;

    private ServerSocket serverSocket;
    private WorkerPool[] workerPools;

    private final ReentrantLock lock;

    private boolean running;
    private int     clientCounter = 0;

    /* Shared services */
    private final AuthService authService;
    private final Repository  repository;

    public Server(int port, int maxDays, int maxSeriesInMemory) throws IOException {
        this.port              = port;
        this.maxDays           = maxDays;
        this.maxSeriesInMemory = maxSeriesInMemory;

        this.lock = new ReentrantLock();

        this.running = false;

        /* Initialize shared services */
        this.authService = new AuthService();
        this.repository  = new Repository(maxDays, maxSeriesInMemory);
    }

    /**
     * Start the server and accept client connections
     */
    public void start() throws IOException {
        start(null);
    }

    /**
     * Start the server and accept client connections, signaling when ready.
     *
     * @param onReady Optional runnable invoked after the server socket is
     *                bound and state set to running
     */
    public void start(Runnable onReady) throws IOException {
        serverSocket = new ServerSocket(port);

        /* Creates multiple worker pools to shard task processing and reduce lock contention. */
        int numPools       = 4;
        int totalWorkers   = Math.max(4, Runtime.getRuntime().availableProcessors() * 2);
        int workersPerPool = Math.max(1, totalWorkers / numPools);

        workerPools = new WorkerPool[numPools];
        for (int i = 0; i < numPools; i++) {
            workerPools[i] = new WorkerPool(workersPerPool, 1024);
            workerPools[i].start();
        }

        /*
         * Even though this function is only called by the main thread,
         * we still need to lock because the running variable is shared
         * with the JVM shutdown hook which can be called at any time.
         */
        lock.lock();
        try {
            running = true;
        } finally {
            lock.unlock();
        }

        if (onReady != null) {
            onReady.run();
        }

        System.out.printf(
            "Sales Server listening on port %d [D:%d, S:%d]%n",
            port, maxDays, maxSeriesInMemory
        );

        /* Accept connections loop */
        while (isRunning()) {
            try {
                Socket clientSocket = serverSocket.accept();

                /* Distribute to worker pools round-robin */
                WorkerPool assignedPool = workerPools[clientCounter % numPools];

                /* Create handler */
                TaggedConnection connection = new TaggedConnection(clientSocket);
                ClientHandler handler = new ClientHandler(
                    connection, authService, repository, assignedPool
                );

                Thread handlerThread = new Thread(handler, "ClientHandler-" + (clientCounter++));
                handlerThread.setDaemon(true);
                handlerThread.start();

            } catch (IOException e) {
                /* Check if we stopped intentionally */
                if (!isRunning()) {
                    /* Intentional shutdown */
                    break;
                }
                System.err.println("Error accepting connection: " + e.getMessage());
            }
        }
    }

    /**
     * Stop the server and cleanup resources
     */
    public void stop() {
        lock.lock();
        try {
            if (!running) {
                return;
            }
            running = false;
        } finally {
            lock.unlock();
        }

        /* Close server socket */
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing server socket: " + e.getMessage());
            }
        }

        /* Shutdown worker pools */
        if (workerPools != null) {
            for (WorkerPool wp : workerPools) {
                wp.stop();
            }
        }

        /* Close services */
        if (repository != null) {
            repository.close();
        }

        System.out.println("Server stopped");
    }

    /**
     * Checks whether the server is currently running.
     *
     * @return {@code true} if the server is running, {@code false} otherwise
     */
    private boolean isRunning() {
        lock.lock();
        try {
            return running;
        } finally {
            lock.unlock();
        }
    }
}
