package uminho.sd.client;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import uminho.sd.protocol.SalesEvent;

/**
 * Interactive terminal UI for the sales service client.
 */
public class ClientUI {

    private final String host;
    private final int    port;

    private final Scanner scanner;

    private SalesClient client;
    private String lastMessage = null;

    private static final String HEADER =
        "Sales Service Client\n" +
        "────────────────────\n";

    private static final String MENU =
        "1  Login\n" +
        "2  Register\n" +
        "3  Add Event\n" +
        "4  Query Sales Quantity\n" +
        "5  Query Sales Volume\n" +
        "6  Query Average Price\n" +
        "7  Query Maximum Price\n" +
        "8  Filter Events\n" +
        "9  Wait for Simultaneous Sales\n" +
        "10 Wait for Consecutive Sales\n" +
        "11 Start New Day (Admin)\n\n" +
        "0  Exit\n";

    /**
     * Initializes the client UI with server connection parameters.
     *
     * @param host server hostname or IP address
     * @param port server port number
     */
    public ClientUI(String host, int port) {
        this.host = host;
        this.port = port;

        this.scanner = new Scanner(System.in);
    }

    /**
     * Entry point for the client execution.
     *
     * Establishes a connection to the server, initializes the user interface,
     * and enters the main menu-processing loop. User input is handled iteratively
     * until an explicit exit condition or a fatal interruption occurs.
     */
    public void run() {
        try {
            connectToServer();
            menuLoop();
        } catch (IOException e) {
            System.out.println("Connection failed: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Operation interrupted");
            Thread.currentThread().interrupt();
        } finally {
            cleanup();
        }
    }

    private void connectToServer() throws IOException, InterruptedException {
        client = new SalesClient(host, port);
        ConsoleUtils.clearScreen();

        System.out.println(
            "Connected successfully to " + host + ":" + port + "!"
        ); Thread.sleep(2000);
    }

    private void menuLoop() throws InterruptedException {
        for( ; ; ) {
            showMenu();
            int choice = readInt("Choice: ");

            try {
                if (!handleChoice(choice)) {
                    break;
                }
            } catch (InterruptedException e) {
                lastMessage = "Operation interrupted";
                Thread.currentThread().interrupt();
                break;
            } catch (IOException e) {
                lastMessage = "Error: " + e.getMessage();
            } catch (Exception e) {
                lastMessage = "Unexpected Error: " + e.getMessage();
            }
        }
    }

    private void showMenu() {
        ConsoleUtils.clearScreen();

        System.out.println(HEADER);
        System.out.println(
            "Status: " + (
                client != null && client.isAuthenticated() ?
                "Authenticated" : "Not authenticated"
            )
        );

        if (lastMessage != null) {
            System.out.println("\n> " + lastMessage + "\n");
            lastMessage = null;
        } else {
            System.out.println();
        }

        System.out.println(MENU);
    }

    private boolean handleChoice(int choice) throws IOException, InterruptedException {
        switch (choice) {
            case 1  -> handleLogin();
            case 2  -> handleRegister();
            case 3  -> handleAddEvent();
            case 4  -> handleSalesQuantity();
            case 5  -> handleSalesVolume();
            case 6  -> handleAvgPrice();
            case 7  -> handleMaxPrice();
            case 8  -> handleFilterEvents();
            case 9  -> handleSimultaneousSales();
            case 10 -> handleConsecutiveSales();
            case 11 -> handleStartNewDay();
            case 0  -> {
                return false;
            }
            default -> lastMessage = "Invalid choice";
        }
        return true;
    }

    /* ---- Handler Methods ---- */

    private void handleLogin() throws IOException, InterruptedException {
        System.out.println("\n─── Login ───");

        String username = readString("Username: ");
        String password = readString("Password: ");

        client.login(username, password);
        lastMessage = "Logged in successfully as " + username;
    }

    private void handleRegister() throws IOException, InterruptedException {
        System.out.println("\n─── Register ───");

        String username = readString("Username: ");
        String password = readString("Password: ");

        client.register(username, password);
        lastMessage = "Registered in successfully as " + username;
    }

    private void handleAddEvent() throws IOException, InterruptedException {
        System.out.println("\n─── Add Event ───");

        String product  = readString("Product name: ");
        int    quantity = readInt("Quantity: ");
        double price    = readDouble("Price: ");

        client.addEvent(product, quantity, price);
        lastMessage = "Event added successfully";
    }

    private void handleSalesQuantity() throws IOException, InterruptedException {
        System.out.println("\n─── Sales Quantity Query ───");

        String product = readString("Product name: ");
        int    days    = readInt("Number of days: ");

        int quantity = client.getSalesQuantity(product, days);
        lastMessage = "Sales quantity: " + quantity;
    }

    private void handleSalesVolume() throws IOException, InterruptedException {
        System.out.println("\n─── Sales Volume Query ───");

        String product = readString("Product name: ");
        int    days    = readInt("Number of days: ");

        double volume = client.getSalesVolume(product, days);
        lastMessage = String.format("Sales volume: %.2f", volume);
    }

    private void handleAvgPrice() throws IOException, InterruptedException {
        System.out.println("\n─── Average Price Query ───");

        String product = readString("Product name: ");
        int    days    = readInt("Number of days: ");

        double avgPrice = client.getAvgPrice(product, days);
        lastMessage = String.format("Average price: %.2f", avgPrice);
    }

    private void handleMaxPrice() throws IOException, InterruptedException {
        System.out.println("\n─── Maximum Price Query ───");

        String product = readString("Product name: ");
        int    days    = readInt("Number of days: ");

        double maxPrice = client.getMaxPrice(product, days);
        lastMessage = String.format("Maximum price: %.2f", maxPrice);
    }

    private void handleFilterEvents() throws IOException, InterruptedException {
        System.out.println("\n─── Filter Events ───");

        Set<String> products = new HashSet<>();
        int count = readInt("Number of products to filter: ");
        for (int i = 0; i < count; i++) {
            String product = readString("Product " + (i + 1) + ": ");
            products.add(product);
        }

        int dayOffset = readInt(
            "Day offset (1=yesterday, 2=day before, etc.): "
        );

        List<SalesEvent> events = client.filterEvents(products, dayOffset);
        StringBuilder sb = new StringBuilder();
        sb.append("Found ").append(events.size()).append(" events:\n");
        for (SalesEvent event : events) {
            sb.append("  ").append(event).append("\n");
        }
        lastMessage = sb.toString();
    }

    private void handleSimultaneousSales() throws IOException, InterruptedException {
        System.out.println("\n─── Wait for Simultaneous Sales ───");

        String p1 = readString("First product: ");
        String p2 = readString("Second product: ");

        System.out.println("Waiting for simultaneous sales...");
        boolean result = client.waitSimultaneousSales(p1, p2);

        if (result) {
            lastMessage = "Both products were sold!";
        } else {
            lastMessage = "Day ended before both products were sold";
        }
    }

    private void handleConsecutiveSales() throws IOException, InterruptedException {
        System.out.println("\n─── Wait for Consecutive Sales ───");

        int n = readInt("Number of consecutive sales: ");

        System.out.println("Waiting for " + n + " consecutive sales...");
        String product = client.waitConsecutiveSales(n);

        if (product != null) {
            lastMessage = "Found " + n + " consecutive sales of: " + product;
        } else {
            lastMessage = "Day ended before " + n + " consecutive sales";
        }
    }

    private void handleStartNewDay() throws IOException, InterruptedException {
        System.out.println("\n─── Start New Day ───");
        System.out.print("Are you sure? (y/n): ");

        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("y") || confirm.equals("yes")) {
            client.startNewDay();
            lastMessage = "New day started";
        } else {
            lastMessage = "Cancelled";
        }
    }

    /* ---- Input Helpers ---- */

    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        for( ; ; ) {
            try {
                System.out.print(prompt);
                String line = scanner.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, try again");
            }
        }
    }

    private double readDouble(String prompt) {
        for( ; ; ) {
            try {
                System.out.print(prompt);
                String line = scanner.nextLine().trim();
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, try again");
            }
        }
    }

    /* ---- Cleanup ---- */

    private void cleanup() {
        try {
            ConsoleUtils.clearScreen();
            if (client != null && !client.isClosed()) {
                client.close();
                System.out.println("Disconnected");
            }
        } catch (IOException e) {
            System.out.println("Error during disconnect: " + e.getMessage());
        }

        scanner.close();
    }
}
