package uminho.sd.server;

import org.junit.jupiter.api.*;

import uminho.sd.client.SalesClient;
import uminho.sd.protocol.Frame;
import uminho.sd.protocol.Serialization;
import uminho.sd.protocol.TaggedConnection;

import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Robustness tests for server under stress and edge cases.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerRobustnessTest {

    private static ServerTestHelper serverHelper;
    private static int port;

    @BeforeAll
    static void startServer() throws Exception {
        serverHelper = new ServerTestHelper();
        serverHelper.startServer();
        port = serverHelper.getPort();

        serverHelper.ensureUser("robustuser", "password");
    }

    @AfterAll
    static void stopServer() throws Exception {
        serverHelper.stopServer();
    }

    @BeforeEach
    void setup() throws Exception {
        serverHelper.resetDay("robustuser", "password");
    }


    @Test
    @Order(1)
    @DisplayName("Client sends but never reads responses")
    @Timeout(10)
    void testNonConsumingClient() throws Exception {
        /* Create a client that sends requests but never reads responses */
        Socket socket = new Socket("localhost", port);
        TaggedConnection connection = new TaggedConnection(socket);

        try {
            /* Authenticate first */
            byte[] authPayload = Serialization
                .serializeAuth("robustuser", "password");
            Frame loginRequest = new Frame(1, Frame.LOGIN, authPayload);
            connection.send(loginRequest);

            Frame loginResponse = connection.receive();
            assertEquals(Frame.RESPONSE_OK, loginResponse.getType(), "Login should succeed");

            /* Now send many requests WITHOUT reading responses */
            int requestCount = 100;
            for (int i = 0; i < requestCount; i++) {
                byte[] eventPayload = Serialization
                    .serializeAddEvent("Product", i, 10.0);
                Frame request = new Frame(i + 2, Frame.ADD_EVENT, eventPayload);
                connection.send(request);
                /* DON'T READ RESPONSE */
            }

            /* Verify server is still responsive to OTHER clients */
            try (SalesClient normalClient = new SalesClient("localhost", port)) {
                normalClient.login("robustuser", "password");

                /* This should work despite non-consuming client */
                assertDoesNotThrow(() -> normalClient.addEvent(
                    "TestProduct", 1, 5.0)
                );

                System.out.println("Server remains responsive despite non-consuming client");
            }

            /*
             * Eventually, the non-consuming client's socket buffers will fill up
             * TCP flow control will prevent further sends - this is expected behavior
             */

        } finally {
            connection.close();
        }
    }
}
