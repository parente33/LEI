package uminho.sd.server;

import org.junit.jupiter.api.*;

/**
 * Scalability tests for server performance.
 * Tests increasing client load.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerScalabilityTest {

    private static ServerTestHelper serverHelper;

    @BeforeAll
    static void startServer() throws Exception {
        serverHelper = new ServerTestHelper();

        serverHelper.startServer();
        serverHelper.ensureUser("scaleuser", "password");
    }

    @AfterAll
    static void stopServer() throws Exception {
        serverHelper.stopServer();
    }

    @BeforeEach
    void setup() throws Exception {
        serverHelper.resetDay("scaleuser", "password");
    }


    @Test
    @Order(1)
    @DisplayName("Scalability: 1 Client")
    void test1ClientBaseline() throws Exception {
        testWithClientCount(1, 1000);
    }

    @Test
    @Order(2)
    @DisplayName("Scalability: 5 Clients")
    void test5Clients() throws Exception {
        testWithClientCount(5, 1000);
    }

    @Test
    @Order(3)
    @DisplayName("Scalability: 10 Clients")
    void test10Clients() throws Exception {
        testWithClientCount(10, 1000);
    }

    @Test
    @Order(4)
    @DisplayName("Scalability: 20 Clients")
    void test20Clients() throws Exception {
        testWithClientCount(20, 1000);
    }

    @Test
    @Order(5)
    @DisplayName("Scalability: 50 Clients")
    void test50Clients() throws Exception {
        testWithClientCount(50, 1000);
    }

    @Test
    @Order(6)
    @DisplayName("Scalability: 100 Clients")
    void test100Clients() throws Exception {
        testWithClientCount(100, 1000);
    }

    /* ---- Generic Client Load Test ---- */

    private void testWithClientCount(int clientCount, int requestsPerClient) throws Exception {
        ServerTestHelper.TestMetrics metrics = serverHelper.runConcurrentClients(
            clientCount,
            "scaleuser",
            "password",
            (client, index, m) -> {
                for (int j = 0; j < requestsPerClient; j++) {
                    long start = System.nanoTime();
                    client.addEvent("Product" + (j % 10), j + 1, 10.0 + j);
                    long end = System.nanoTime();

                    m.recordSuccess(end - start);
                }
            }
        );

        metrics.assertSuccess(clientCount * requestsPerClient);
    }
}
