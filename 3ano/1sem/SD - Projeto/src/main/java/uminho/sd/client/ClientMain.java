package uminho.sd.client;

/**
 * Entry point for the client application.
 * Launches the interactive terminal UI.
 */
public class ClientMain {

    public static void main(String[] args) {
        int exitCode = new ClientMain().run(args);
        System.exit(exitCode);
    }

    private int run(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: mvn exec:java@run-client <host> <port>");
            return 1;
        }

        final String host = args[0];
        final int    port;

        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Error: Port must be a number");
            return 1;
        }

        ClientUI ui = new ClientUI(host, port);
        ui.run();

        return 0;
    }
}
