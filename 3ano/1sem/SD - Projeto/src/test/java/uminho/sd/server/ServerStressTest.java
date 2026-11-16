package uminho.sd.server;

import org.junit.jupiter.api.*;

import java.util.Random;

import uminho.sd.client.SalesClient;

/**
 * Stress tests for server.
 * Randomly performs operations on the server.
 */
public class ServerStressTest {

    private static ServerTestHelper serverHelper;

    @BeforeAll
    static void startServer() throws Exception {
        serverHelper = new ServerTestHelper();
        serverHelper.startServer(50, 5);
        serverHelper.ensureUser("stressuser", "password");
    }

    @AfterAll
    static void stopServer() throws Exception {
        serverHelper.stopServer();
    }


    @Test
    @DisplayName("Stress: Aggressive random workload")
    @Timeout(600)
    void testChaosWorkload() throws Exception {
        int clientCount         = 100;
        int operationsPerClient = 1000000;

        System.out.println("\n─── Starting Stress Test ───");
        System.out.println("Clients:    " + clientCount);
        System.out.println("Ops/Client: " + operationsPerClient);
        System.out.println("Total Ops:  " + (clientCount * operationsPerClient) + "\n");

        try (SalesClient setup = serverHelper.newLoggedClient(
             "stressuser", "password")
        ) {
            for (int d = 0; d < 5; d++) {
                for (int i = 0; i < 100; i++) {
                    setup.addEvent("Product" + (i % 10), i + 1, 10.0 + d);
                }
                setup.startNewDay();
                Thread.sleep(10);
            }
        }

        ServerTestHelper.TestMetrics metrics = serverHelper.runConcurrentClients(
            clientCount,
            "stressuser",
            "password",
            (client, index, m) -> {
                Random rand = new Random();
                for (int j = 0; j < operationsPerClient; j++) {
                    double r       = rand.nextDouble();
                    String product = "Product" + rand.nextInt(50);

                    if (r < 0.70) {
                        client.addEvent(product, rand.nextInt(10) + 1, rand.nextDouble() * 100);
                    } else if (r < 0.85) {
                        client.getSalesQuantity(product, rand.nextInt(10) + 1);
                    } else if (r < 0.95) {
                        client.getSalesVolume(product, rand.nextInt(10) + 1);
                    } else if (r < 0.999999) {
                        if (rand.nextBoolean()) {
                            client.getAvgPrice(product, rand.nextInt(10) + 1);
                        } else {
                            client.getMaxPrice(product, rand.nextInt(10) + 1);
                        }
                    } else {
                        client.startNewDay();
                    }
                }
                m.recordSuccesses(operationsPerClient);
            }
        );

        metrics.assertSuccess(clientCount * operationsPerClient);
    }
}
