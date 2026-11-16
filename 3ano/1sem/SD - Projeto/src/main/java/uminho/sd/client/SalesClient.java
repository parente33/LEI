package uminho.sd.client;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Set;

import uminho.sd.protocol.Frame;
import uminho.sd.protocol.TaggedConnection;
import uminho.sd.protocol.Demultiplexer;
import uminho.sd.protocol.Serialization;
import uminho.sd.protocol.SalesEvent;

/**
 * High-level client library for sales service.
 */
public class SalesClient implements AutoCloseable {

    private final String host;
    private final int    port;

    private TaggedConnection connection;
    private Demultiplexer    demux;

    private boolean authenticated;
    private boolean closed;

    /**
     * Initializes a new SalesClient connected to the specified host and port.
     *
     * The client is thread-safe and supports concurrent requests.
     *
     * @param host Server hostname
     * @param port Server port
     */
    public SalesClient(String host, int port) throws IOException {
        this.host = host;
        this.port = port;

        this.authenticated = false;
        this.closed        = false;
        connect();
    }

    private void connect() throws IOException {
        Socket socket = new Socket(host, port);

        this.connection = new TaggedConnection(socket);
        this.demux      = new Demultiplexer(connection);
    }

    /* ---- Authentication ---- */

    /**
     * Registers a new user with the service.
     *
     * @param username Username (non-null, non-empty)
     * @param password Password (non-null, non-empty)
     */
    public void register(String username, String password)
            throws IOException, InterruptedException {
        ensureNotClosed();
        validateAuth(username, password);

        byte[] payload = Serialization.serializeAuth(username, password);
        Frame response = demux.send(Frame.REGISTER, payload);

        checkErrorResponse(response);
    }

    /**
     * Authenticates an existing user.
     * Must be called before performing any operations.
     *
     * @param username Username
     * @param password Password
     */
    public void login(String username, String password)
            throws IOException, InterruptedException {
        ensureNotClosed();
        validateAuth(username, password);

        byte[] payload = Serialization.serializeAuth(username, password);
        Frame response = demux.send(Frame.LOGIN, payload);

        checkErrorResponse(response);
        this.authenticated = true;
    }

    /* ---- Event Logging ---- */

    /**
     * Logs a new sales event.
     *
     * @param product  Product name (non-null, non-empty)
     * @param quantity Quantity sold (must be > 0)
     * @param price    Price per unit (must be >= 0)
     */
    public void addEvent(String product, int quantity, double price)
            throws IOException, InterruptedException {
        ensureAuthenticated();
        if (product == null || product.trim().isEmpty()) {
            throw new IllegalArgumentException("Product cannot be null or empty");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        byte[] payload = Serialization.serializeAddEvent(product, quantity, price);
        Frame response = demux.send(Frame.ADD_EVENT, payload);

        checkErrorResponse(response);
    }

    /* ---- Queries ---- */

    /**
     * Queries the total quantity of a product sold over the last N days.
     *
     * @param product Product name
     * @param days    Number of days to look back
     * @return Total quantity
     */
    public int getSalesQuantity(String product, int days)
            throws IOException, InterruptedException {
        ensureAuthenticated();
        validateQuery(product, days);

        byte[] payload = Serialization.serializeProductDaysQuery(product, days);
        Frame response = demux.send(Frame.QUERY_SALES_QUANTITY, payload);

        checkErrorResponse(response);
        return Serialization.deserializeInt(response.getPayload());
    }

    /**
     * Queries the total sales volume (quantity * price) for a product.
     *
     * @param product Product name
     * @param days    Number of days to look back
     * @return Total volume
     */
    public double getSalesVolume(String product, int days)
            throws IOException, InterruptedException {
        ensureAuthenticated();
        validateQuery(product, days);

        byte[] payload = Serialization.serializeProductDaysQuery(product, days);
        Frame response = demux.send(Frame.QUERY_SALES_VOLUME, payload);

        checkErrorResponse(response);
        return Serialization.deserializeDouble(response.getPayload());
    }

    /**
     * Queries the average price of a product over the last N days.
     *
     * @param product Product name
     * @param days    Number of days to look back
     * @return Average price, or 0.0 if no sales
     */
    public double getAvgPrice(String product, int days)
            throws IOException, InterruptedException {
        ensureAuthenticated();
        validateQuery(product, days);

        byte[] payload = Serialization.serializeProductDaysQuery(product, days);
        Frame response = demux.send(Frame.QUERY_AVG_PRICE, payload);

        checkErrorResponse(response);
        return Serialization.deserializeDouble(response.getPayload());
    }

    /**
     * Queries the maximum price of a product over the last N days.
     *
     * @param product Product name
     * @param days    Number of days to look back
     * @return Maximum price
     */
    public double getMaxPrice(String product, int days)
            throws IOException, InterruptedException {
        ensureAuthenticated();
        validateQuery(product, days);

        byte[] payload = Serialization.serializeProductDaysQuery(product, days);
        Frame response = demux.send(Frame.QUERY_MAX_PRICE, payload);

        checkErrorResponse(response);
        return Serialization.deserializeDouble(response.getPayload());
    }

    /* ---- Filtering ---- */

    /**
     * Retrieves a list of events matching specific products on a specific
     * historical day.
     *
     * @param productNames Set of product names to filter by
     * @param dayOffset    Offset from current day (e.g., 1 = yesterday)
     * @return List of matching SalesEvents
     */
    public List<SalesEvent> filterEvents(Set<String> productNames, int dayOffset)
            throws IOException, InterruptedException {
        ensureAuthenticated();
        if (productNames == null || productNames.isEmpty()) {
            throw new IllegalArgumentException("Product names set cannot be empty");
        }
        if (dayOffset < 0) {
            throw new IllegalArgumentException("Day offset cannot be negative");
        }

        byte[] payload = Serialization.serializeFilterEventsRequest(productNames, dayOffset);
        Frame response = demux.send(Frame.FILTER_EVENTS, payload);

        checkErrorResponse(response);
        return Serialization.deserializeEventList(response.getPayload());
    }

    /* ---- Notifications (Blocking) ---- */

    /**
     * Blocks until a simultaneous sale of two products occurs.
     *
     * @param p1 First product
     * @param p2 Second product
     * @return True if occurred
     */
    public boolean waitSimultaneousSales(String p1, String p2)
            throws IOException, InterruptedException {
        ensureAuthenticated();
        if (p1 == null || p2 == null) {
            throw new IllegalArgumentException("Products cannot be null");
        }

        byte[] payload = Serialization.serializeSimultaneousSales(p1, p2);
        Frame response = demux.send(Frame.NOTIFY_SIMULTANEOUS, payload);

        checkErrorResponse(response);
        return Serialization.deserializeBoolean(response.getPayload());
    }

    /**
     * Blocks until a sequence of N sales occurs.
     *
     * @param n Sequence length
     * @return The product involved in the sequence, or null if timed out/error
     */
    public String waitConsecutiveSales(int n)
            throws IOException, InterruptedException {
        ensureAuthenticated();
        if (n <= 0) {
            throw new IllegalArgumentException("Sequence length must be positive");
        }

        byte[] payload = Serialization.serializeConsecutiveSales(n);
        Frame response = demux.send(Frame.NOTIFY_CONSECUTIVE, payload);

        checkErrorResponse(response);
        return Serialization.deserializeNullableString(response.getPayload());
    }

    /* ---- Admin Operations ---- */

    /**
     * Force start a new day.
     */
    public void startNewDay() throws IOException, InterruptedException {
        ensureAuthenticated();

        byte[] payload = Serialization.serializeVoid();
        Frame response = demux.send(Frame.START_NEW_DAY, payload);

        checkErrorResponse(response);
    }

    /* ---- Resource Management ---- */

    @Override
    public void close() throws IOException {
        if (!closed) {
            closed = true;
            if (demux != null) {
                demux.close();
            }
            /* demux.close() also closes connection, but we can be explicit */
            if (connection != null) {
                connection.close();
            }
        }
    }

    /* ---- Helper Methods ---- */

    /**
     * Checks if the server returned an error frame.
     */
    private void checkErrorResponse(Frame response) throws IOException {
        if (response.getType() == Frame.RESPONSE_ERROR) {
            String errorMsg = Serialization.deserializeError(response.getPayload());
            throw new IOException("Server error: " + errorMsg);
        }
    }

    /**
     * Ensures client is authenticated before executing protected operations.
     */
    private void ensureAuthenticated() throws IOException {
        ensureNotClosed();
        if (!authenticated) {
            throw new IOException("Not authenticated!");
        }
    }

    /**
     * Ensures the client connection is still open.
     */
    private void ensureNotClosed() throws IOException {
        if (closed) {
            throw new IOException("Client is closed");
        }
    }

    /**
     * Validates that the username and password are non-null and non-empty.
     *
     * @param u the username
     * @param p the password
     * @throws IllegalArgumentException if the product is null/empty or day offset is negative
     */
    private void validateAuth(String u, String p) {
        if (u == null || u.trim().isEmpty() || p == null || p.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Username/Password cannot be empty"
            );
        }
    }

    /**
     * Validates a product query.
     *
     * @param p the product name
     * @param d the day offset
     * @throws IllegalArgumentException if the product is null/empty or day offset is negative
     */
    private void validateQuery(String p, int d) {
        if (p == null || p.trim().isEmpty()) {
            throw new IllegalArgumentException("Product cannot be empty");
        }
        if (d < 0) {
            throw new IllegalArgumentException("Days cannot be negative");
        }
    }

    /**
     * Checks whether the client is authenticated.
     *
     * @return {@code true} if authenticated, {@code false} otherwise
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * Checks whether the client connection is closed.
     *
     * @return {@code true} if closed, {@code false} otherwise
     */
    public boolean isClosed() {
        return closed;
    }
}
