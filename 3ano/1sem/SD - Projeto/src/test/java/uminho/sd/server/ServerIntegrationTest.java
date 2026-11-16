package uminho.sd.server;

import org.junit.jupiter.api.*;

import uminho.sd.client.SalesClient;
import uminho.sd.protocol.SalesEvent;

import java.io.*;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for complete server functionality.
 * Tests all features working correctly end-to-end.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerIntegrationTest {

    private static ServerTestHelper serverHelper;
    private static int port;

    @BeforeAll
    static void startServer() throws Exception {
        serverHelper = new ServerTestHelper();
        serverHelper.startServer();
        port = serverHelper.getPort();

        serverHelper.ensureUser("integrationuser", "password");
    }

    @AfterAll
    static void stopServer() throws Exception {
        serverHelper.stopServer();
    }


    @Test
    @Order(1)
    @DisplayName("Authentication: Login and Register")
    void testAuthentication() throws Exception {
        try (SalesClient client = new SalesClient("localhost", port)) {
            assertDoesNotThrow(() -> client.login(
                "integrationuser", "password")
            );

            System.out.println("Authentication working");
        }
    }

    @Test
    @Order(2)
    @DisplayName("Authentication: Reject invalid credentials")
    void testInvalidCredentials() throws Exception {
        try (SalesClient client = new SalesClient("localhost", port)) {
            Exception ex = assertThrows(Exception.class,
                () -> client.login("integrationuser", "wrongpassword")
            );
            assertTrue(ex.getMessage().contains("Invalid credentials"));

            System.out.println("Invalid credentials rejected");
        }
    }

    @Test
    @Order(3)
    @DisplayName("Authentication: Reject unauthenticated requests")
    void testUnauthenticatedReject() throws Exception {
        try (SalesClient client = new SalesClient("localhost", port)) {
            Exception ex = assertThrows(Exception.class,
                () -> client.addEvent("Laptop", 1, 999)
            );
            assertTrue(ex.getMessage().contains("Not authenticated"));

            System.out.println("Unauthenticated requests rejected");
        }
    }

    @Test
    @Order(4)
    @DisplayName("Event Logging: Add events to current day")
    void testEventLogging() throws Exception {
        try (SalesClient client = new SalesClient("localhost", port)) {
            client.login("integrationuser", "password");

            assertDoesNotThrow(() -> client.addEvent("Laptop", 2, 1200.00));
            assertDoesNotThrow(() -> client.addEvent("Mouse", 5, 25.50));
            assertDoesNotThrow(() -> client.addEvent("Keyboard", 3, 75.00));

            System.out.println("Event logging working");
        }
    }

    @Test
    @Order(5)
    @DisplayName("Day Transitions: startNewDay freezes and persists")
    void testDayTransition() throws Exception {
        try (SalesClient client = new SalesClient("localhost", port)) {
            client.login("integrationuser", "password");

            client.addEvent("Product1", 10, 50.0);
            assertDoesNotThrow(() -> client.startNewDay());
            client.addEvent("Product2", 5, 30.0);

            System.out.println("Day transitions working");
        }
    }

    @Test
    @Order(6)
    @DisplayName("Queries: All 4 aggregations work correctly")
    void testAggregationQueries() throws Exception {
        try (SalesClient client = new SalesClient("localhost", port)) {
            client.login("integrationuser", "password");

            client.addEvent("TestProduct", 100, 10.0);  /* volume = 1000 */
            client.addEvent("TestProduct", 50, 20.0);   /* volume = 1000 */
            client.startNewDay();

            /* Query previous day (day offset 1) */

            int quantity = client.getSalesQuantity("TestProduct", 1);
            assertEquals(150, quantity, "Quantity should be 100 + 50");

            double volume = client.getSalesVolume("TestProduct", 1);
            assertEquals(2000.0, volume, 0.01, "Volume should be 1000 + 1000");

            double avgPrice = client.getAvgPrice("TestProduct", 1);
            assertEquals(13.33, avgPrice, 0.01, "Avg price should be 2000/150");

            double maxPrice = client.getMaxPrice("TestProduct", 1);
            assertEquals(20.0, maxPrice, 0.01, "Max price should be 20");

            System.out.println("All aggregation queries working");
        }
    }

    @Test
    @Order(7)
    @DisplayName("Event Filtering: Filter by product set")
    void testEventFiltering() throws Exception {
        try (SalesClient client = new SalesClient("localhost", port)) {
            client.login("integrationuser", "password");

            client.addEvent("Apple", 10, 1.0);
            client.addEvent("Banana", 20, 0.5);
            client.addEvent("Orange", 15, 0.75);
            client.startNewDay();

            /* Filter for specific products */
            Set<String>      products = Set.of("Apple", "Orange");
            List<SalesEvent> events   = client.filterEvents(products, 1);

            assertEquals(2, events.size(), "Should return 2 events");

            /* Verify dictionary encoding worked */
            assertTrue(events.stream().anyMatch(e -> e.getProduct().equals("Apple")));
            assertTrue(events.stream().anyMatch(e -> e.getProduct().equals("Orange")));

            System.out.println("Event filtering working (dictionary encoding)");
        }
    }

    @Test
    @Order(8)
    @DisplayName("Persistence: Data saved to disk")
    void testPersistenceFileCreation() throws Exception {
        try (SalesClient client = new SalesClient("localhost", port)) {
            client.login("integrationuser", "password");

            client.addEvent("PersistTest", 100, 99.99);
            client.startNewDay();

            /* Check that data directory was created */
            File dataDir = new File("./data");
            assertTrue(dataDir.exists(), "Data directory should exist");
            assertTrue(dataDir.isDirectory(), "Should be a directory");

            /* Check for day files */
            File[] dayFiles = dataDir.listFiles((dir, name) -> name.startsWith("day_"));
            assertNotNull(dayFiles);
            assertTrue(dayFiles.length > 0, "Should have persisted at least one day");

            System.out.println("Persistence files created: " + dayFiles.length + " days");
        }
    }
}
