package mp.net.sockets.tests;

import mp.net.sockets.ServerService;
import mp.net.sockets.tests.mocks.MockMessageConnectionHandler;
import mp.net.sockets.tests.mocks.MockMessageSocket;
import mp.net.sockets.tests.mocks.InertObjectSocketAdapter;
import mp.net.sockets.protocols.ObjectSocket;
import org.junit.jupiter.api.*;

import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

@DisplayName("Socket Connections")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestSocketConnections {

    private final String
        VALID_HOST = "localhost",
        INVALID_HOST = "invalid";

    private final int
        VALID_PORT = 717,
        INVALID_PORT = 666;


    //-

    @Test
    @Order(1)
    @DisplayName("Start and Stop Server")
    void doNothing() throws Exception {
        try (ServerService ignored = ServerService.with(VALID_PORT, MockMessageConnectionHandler.class)) {

            // give our service a beat to boot before we close it
            Thread.sleep(100);

        }
    }

    @Test
    @Order(2)
    @DisplayName("Fail to Connect to Invalid Address")
    void failToConnectToInvalidAddress() throws Exception {

        try (ServerService ignored = ServerService.with(VALID_PORT, MockMessageConnectionHandler.class)) {

            // create a mock connection driver
            try (ObjectSocket connectionDriver = new InertObjectSocketAdapter()) {

                // assert that we cannot connect to an invalid address
                Assertions.assertThrowsExactly(UnknownHostException.class,
                        () -> connectionDriver.listenToSocket(new Socket(INVALID_HOST, INVALID_PORT)));

            }

        }

    }

    @Test
    @Order(3)
    @DisplayName("Fail to Connect to Invalid Port")
    void failToConnectToInvalidPort() throws Exception {

        try (ServerService ignored = ServerService.with(VALID_PORT, MockMessageConnectionHandler.class)) {

            // create a mock connection driver
            try (ObjectSocket connectionDriver = new InertObjectSocketAdapter()) {

                // assert that we cannot connect to an invalid port
                Assertions.assertThrowsExactly(ConnectException.class,
                        () -> connectionDriver.listenToSocket(new Socket(VALID_HOST, INVALID_PORT)));

            }

        }

    }

    @Test
    @Order(4)
    @DisplayName("Connect")
    void connectAndDisconnect() throws Exception {

        try (ServerService ignored = ServerService.with(VALID_PORT, MockMessageConnectionHandler.class)) {

            // create a mock connection driver
            try (MockMessageSocket connectionDriver = new MockMessageSocket()) {

                // connect to our test server without throwing an error
                Assertions.assertDoesNotThrow(() -> connectionDriver.listenToSocket(new Socket(VALID_HOST, VALID_PORT)));

            }

        }

    }

    @Test
    @Order(5)
    @DisplayName("Disconnect")
    void disconnect() throws Exception {

        try (ServerService ignored = ServerService.with(VALID_PORT, MockMessageConnectionHandler.class)) {

            // create a mock connection driver
            try (MockMessageSocket connectionDriver = new MockMessageSocket()) {

                // connect to our test server
                connectionDriver.listenToSocket(new Socket(VALID_HOST, VALID_PORT));

                // disconnect from the server without throwing an error
                Assertions.assertDoesNotThrow(connectionDriver::close);

            }

        }

    }
}
