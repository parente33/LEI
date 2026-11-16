package uminho.sd.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * Shared serialization utilities for the protocol.
 *
 * Client uses: serialize* methods to build requests, deserialize* for responses
 * Server uses: deserialize* methods to parse requests, serialize* for responses
 */
public class Serialization {

    private static final byte[] EMPTY_PAYLOAD = new byte[0];

    /**
     * Private constructor to prevent instantiation.
     */
    private Serialization() {
        /* utility class */
    }

    /* ---- Authentication Requests ---- */

    /**
     * Serializes login/register credentials.
     * output: [username:UTF][password:UTF]
     *
     * @param username The username
     * @param password The password
     * @return Serialized payload
     */
    public static byte[] serializeAuth(String username, String password)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream      dos  = new DataOutputStream(baos);

        dos.writeUTF(username);
        dos.writeUTF(password);

        return baos.toByteArray();
    }

    /**
     * Deserializes login/register credentials.
     *
     * @param payload The serialized payload
     * @return Array containing {username, password}
     */
    public static String[] deserializeAuth(byte[] payload) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        DataInputStream      dis  = new DataInputStream(bais);

        String username = dis.readUTF();
        String password = dis.readUTF();

        return new String[] { username, password };
    }

    /* ---- Add Event Request ---- */

    /**
     * Serializes an add-event request.
     * output: [product:UTF][quantity:int][price:double]
     *
     * @param product  Product name
     * @param quantity Quantity sold
     * @param price    Price per unit
     * @return Serialized payload
     */
    public static byte[] serializeAddEvent(
            String product, int quantity, double price) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream      dos  = new DataOutputStream(baos);

        dos.writeUTF(product);
        dos.writeInt(quantity);
        dos.writeDouble(price);

        return baos.toByteArray();
    }

    /**
     * Deserializes an add-event request.
     *
     * @param payload The serialized payload
     * @return Array containing {product:String, quantity:int, price:double}
     */
    public static Object[] deserializeAddEvent(byte[] payload) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        DataInputStream      dis  = new DataInputStream(bais);

        String product  = dis.readUTF();
        int    quantity = dis.readInt();
        double price    = dis.readDouble();

        return new Object[] { product, quantity, price };
    }

    /**
     * Deserializes an add-event request directly from the stream.
     *
     * @param dis The input stream
     * @return SalesEvent object
     */
    public static SalesEvent deserializeAddEventStream(DataInputStream dis) throws IOException {
        String product  = dis.readUTF();
        int    quantity = dis.readInt();
        double price    = dis.readDouble();

        return new SalesEvent(product, quantity, price);
    }

    /* ---- Product-Days Queries ---- */

    /**
     * Serializes a query for a product over a number of days.
     * output: [product:UTF][days:int]
     *
     * @param product Product name
     * @param days    Number of days
     * @return Serialized payload
     */
    public static byte[] serializeProductDaysQuery(String product, int days) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream      dos  = new DataOutputStream(baos);

        dos.writeUTF(product);
        dos.writeInt(days);

        return baos.toByteArray();
    }

    /**
     * Deserializes a product-days query.
     *
     * @param payload The serialized payload
     * @return Array containing {product:String, days:int}
     */
    public static Object[] deserializeProductDaysQuery(byte[] payload) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        DataInputStream      dis  = new DataInputStream(bais);

        String product = dis.readUTF();
        int    days    = dis.readInt();

        return new Object[] { product, days };
    }

    /* ---- Filter Events Request ---- */

    /**
     * Serializes a filter-events request.
     * output: [numProducts:int][product1:UTF]...[dayOffset:int]
     *
     * @param productNames Set of product names to retain
     * @param dayOffset    Day offset to query
     * @return Serialized payload
     */
    public static byte[] serializeFilterEventsRequest(
            Set<String> productNames, int dayOffset) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream      dos  = new DataOutputStream(baos);

        dos.writeInt(productNames.size());
        for (String product : productNames) {
            dos.writeUTF(product);
        }
        dos.writeInt(dayOffset);

        return baos.toByteArray();
    }

    /**
     * Deserializes a filter-events request.
     *
     * @param payload The serialized payload
     * @return Array containing {@code {products: Set<String>, dayOffset: int}}
     */
    public static Object[] deserializeFilterEventsRequest(byte[] payload) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        DataInputStream      dis  = new DataInputStream(bais);

        int numProducts = dis.readInt();
        Set<String> products = new java.util.HashSet<>();
        for (int i = 0; i < numProducts; i++) {
            products.add(dis.readUTF());
        }
        int dayOffset = dis.readInt();

        return new Object[] { products, dayOffset };
    }

    /* ---- Notification Requests ---- */

    /**
     * Serializes a request to wait for simultaneous sales.
     * output: [product1:UTF][product2:UTF]
     *
     * @param p1 First product
     * @param p2 Second product
     * @return Serialized payload
     */
    public static byte[] serializeSimultaneousSales(String p1, String p2) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream      dos  = new DataOutputStream(baos);

        dos.writeUTF(p1);
        dos.writeUTF(p2);

        return baos.toByteArray();
    }

    /**
     * Deserializes a simultaneous sales request.
     *
     * @param payload The serialized payload
     * @return Array containing {product1, product2}
     */
    public static String[] deserializeSimultaneousSales(byte[] payload) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        DataInputStream      dis  = new DataInputStream(bais);

        String p1 = dis.readUTF();
        String p2 = dis.readUTF();

        return new String[] { p1, p2 };
    }

    /**
     * Serializes a request to wait for consecutive sales.
     * output: [n:int]
     *
     * @param n Number of consecutive sales
     * @return Serialized payload
     */
    public static byte[] serializeConsecutiveSales(int n) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream      dos  = new DataOutputStream(baos);

        dos.writeInt(n);

        return baos.toByteArray();
    }

    /**
     * Deserializes a consecutive sales request.
     *
     * @param payload The serialized payload
     * @return The number N
     */
    public static int deserializeConsecutiveSales(byte[] payload) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        DataInputStream      dis  = new DataInputStream(bais);

        return dis.readInt();
    }

    /* ---- Simple Response Types ---- */

    /**
     * Returns an empty payload for void responses.
     */
    public static byte[] serializeVoid() {
        return EMPTY_PAYLOAD;
    }

    /**
     * Serializes an integer into a 4-byte array.
     *
     * @param value the integer to serialize
     * @return a byte array representing the integer
     */
    public static byte[] serializeInt(int value) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream      dos  = new DataOutputStream(baos);

        dos.writeInt(value);

        return baos.toByteArray();
    }

    /**
     * Deserializes an integer from a 4-byte.
     *
     * @param payload the byte array containing the serialized integer
     * @return the deserialized integer
     */
    public static int deserializeInt(byte[] payload) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        DataInputStream      dis  = new DataInputStream(bais);

        return dis.readInt();
    }

    /**
     * Serializes a double into an 8-byte array.
     *
     * @param value the double to serialize
     * @return a byte array representing the double
     */
    public static byte[] serializeDouble(double value) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream      dos  = new DataOutputStream(baos);

        dos.writeDouble(value);

        return baos.toByteArray();
    }

    /**
     * Deserializes a double from an 8-byte array.
     *
     * @param payload the byte array containing the serialized double
     * @return the deserialized double
     */
    public static double deserializeDouble(byte[] payload) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        DataInputStream      dis  = new DataInputStream(bais);

        return dis.readDouble();
    }

    /**
     * Serializes a boolean into a 1-byte array.
     *
     * @param value the boolean to serialize
     * @return a byte array representing the boolean
     */
    public static byte[] serializeBoolean(boolean value) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream      dos  = new DataOutputStream(baos);

        dos.writeBoolean(value);

        return baos.toByteArray();
    }

    /**
     * Deserializes a boolean from a 1-byte array.
     *
     * @param payload the byte array containing the serialized boolean
     * @return the deserialized boolean
     */
    public static boolean deserializeBoolean(byte[] payload) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        DataInputStream      dis  = new DataInputStream(bais);

        return dis.readBoolean();
    }

    /**
     * Serializes a nullable string into a byte array.
     *
     * First writes a boolean indicating nullness:
     * - {@code true} if the string is null
     * - {@code false} if a non-null string follows
     *
     * @param value the string to serialize, may be null
     * @return a byte array representing the nullable string
     */
    public static byte[] serializeNullableString(String value) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream      dos  = new DataOutputStream(baos);

        dos.writeBoolean(value == null);
        if (value != null) {
            dos.writeUTF(value);
        }

        return baos.toByteArray();
    }

    /**
     * Deserializes a nullable string from a byte array.
     *
     * Expects a boolean flag first to indicate nullness:
     * - {@code true} returns null
     * - {@code false} reads the following UTF string
     *
     * @param payload the byte array containing the serialized nullable string
     * @return the deserialized string, or null
     */
    public static String deserializeNullableString(byte[] payload) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        DataInputStream      dis  = new DataInputStream(bais);

        boolean isNull = dis.readBoolean();
        if (isNull) {
            return null;
        }

        return dis.readUTF();
    }

    /* ---- Error Handling ---- */

    /**
     * Serializes an error message into a byte array.
     *
     * @param errorMessage the error message to serialize
     * @return a byte array representing the error message
     */
    public static byte[] serializeError(String errorMessage) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream      dos  = new DataOutputStream(baos);

        dos.writeUTF(errorMessage);

        return baos.toByteArray();
    }

    /**
     * Deserializes an error message from a byte array.
     *
     * If the byte array is empty, returns a default message "Unknown error".
     *
     * @param payload the byte array containing the serialized error message
     * @return the deserialized error message, or "Unknown error" if empty
     */
    public static String deserializeError(byte[] payload) throws IOException {
        if (payload.length == 0) {
            return "Unknown error";
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        DataInputStream      dis  = new DataInputStream(bais);

        return dis.readUTF();
    }

    /* ---- Complex Type: Event List (Dictionary Encoding) ---- */

    /**
     * Serializes a list of sales events using dictionary encoding to save space.
     * Replaces repeated product names with integer indices.
     *
     * @param events List of sales events
     * @return Serialized dictionary-encoded payload
     */
    public static byte[] serializeEventList(List<SalesEvent> events) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream      dos  = new DataOutputStream(baos);

        /* Build product dictionary */
        List<String>         productDict  = new ArrayList<>();
        Map<String, Integer> productIndex = new HashMap<>();

        for (SalesEvent event : events) {
            if (!productIndex.containsKey(event.getProduct())) {
                productIndex.put(event.getProduct(), productDict.size());
                productDict.add(event.getProduct());
            }
        }

        /* Write dictionary */
        dos.writeInt(productDict.size());
        for (String product : productDict) {
            dos.writeUTF(product);
        }

        /* Write events using dictionary indices */
        dos.writeInt(events.size());
        for (SalesEvent event : events) {
            int index = productIndex.get(event.getProduct());
            dos.writeInt(index);
            dos.writeInt(event.getQuantity());
            dos.writeDouble(event.getPrice());
        }

        return baos.toByteArray();
    }

    /**
     * Deserializes a list of sales events from dictionary-encoded payload.
     *
     * @param payload The serialized payload
     * @return List of SalesEvent objects
     */
    public static List<SalesEvent> deserializeEventList(byte[] payload) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        DataInputStream      dis  = new DataInputStream(bais);

        /* Read product dictionary */
        int numProducts = dis.readInt();
        String[] products = new String[numProducts];
        for (int i = 0; i < numProducts; i++) {
            products[i] = dis.readUTF();
        }

        /* Read events */
        int numEvents = dis.readInt();
        List<SalesEvent> events = new ArrayList<>(numEvents);

        for (int i = 0; i < numEvents; i++) {
            int    productIndex = dis.readInt();
            int    quantity     = dis.readInt();
            double price        = dis.readDouble();

            if (productIndex < 0 || productIndex >= numProducts) {
                throw new IOException("Invalid product index: " + productIndex);
            }

            events.add(new SalesEvent(products[productIndex], quantity, price));
        }

        return events;
    }
}
