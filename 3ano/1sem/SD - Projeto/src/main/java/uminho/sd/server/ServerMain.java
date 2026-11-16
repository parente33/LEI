package uminho.sd.server;

import java.io.IOException;

import uminho.sd.server.core.Server;

/**
 * Entry point for the server.
 * Launches the server with specified parameters.
 */
public class ServerMain {

    public static void main(String[] args) {
        int exitCode = new ServerMain().run(args);
        System.exit(exitCode);
    }

    private int run(String[] args) {
        if (args.length < 3) {
            System.err.println(
                "Usage: mvn exec:java@run-server " +
                "<port> <maxDays> <maxSeriesInMemory>"
            );
            return 1;
        }

        final int port;
        final int maxDays;
        final int maxSeriesInMemory;

        try {
            port              = Integer.parseInt(args[0]);
            maxDays           = Integer.parseInt(args[1]);
            maxSeriesInMemory = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.err.println(
                "Error: All parameters must be integers: " +
                "<port> <maxDays> <maxSeriesInMemory>"
            );
            return 1;
        }

        if (maxSeriesInMemory > maxDays) {
            System.err.println(
                "Error: S (max series in memory) must be <= " + "D (max days)"
            );
            System.err.println("Got: S=" + maxSeriesInMemory + ", D=" + maxDays);
            return 1;
        }

        Server server;
        try {
            server = new Server(port, maxDays, maxSeriesInMemory);

            /*
            * Registers a JVM shutdown hook that executes on process termination (normal exit, ctrl+c, or termination signal).
            * Ensures the server stops gracefully, releasing sockets, threads, and other resources.
            */
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nShutting down server...");
                server.stop();
            }));

            server.start();
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            return 1;
        }

        return 0;
    }
}
