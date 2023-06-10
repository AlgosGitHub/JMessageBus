package mp.net.sockets.tests;

import mp.net.sockets.ServerService;
import mp.net.sockets.tests.commands.ReturnObjectCommand;
import mp.net.sockets.tests.mocks.MockMessageConnectionHandler;
import mp.net.sockets.tests.mocks.MockMessageSocket;
import mp.net.sockets.tests.mocks.MockObjectConnectionHandler;
import mp.net.sockets.tests.mocks.MockObjectSocket;
import org.junit.jupiter.api.*;

import java.net.Socket;

@DisplayName("Socket Transmission Protocols")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestSocketTransmissionProtocols {

    private final String TEST_HOST = "localhost";

    private final String TEST_MESSAGE = "Hello, World!";

    private final int TEST_PORT = 616;

    @Test
    @Order(1)
    @DisplayName("Transmit Message")
    void test_transmitMessage() throws Exception {

        //try-with-resources: Mock Client Connector and Server Service w/ Mock Protocol
        try(MockMessageSocket socketClient = new MockMessageSocket();
            ServerService ignored = ServerService.with(TEST_PORT, MockMessageConnectionHandler.class)) {

            //connect the client to our server
            socketClient.listenToSocket(new Socket(TEST_HOST, TEST_PORT));

            //send the test message, it should be returned
            socketClient.sendMessage(TEST_MESSAGE);

            //wait a beat for the async operation to complete
            Thread.sleep(60);

            //assert that our test message was returned by the server (received by the client)
            Assertions.assertEquals(TEST_MESSAGE, socketClient.lastMessageReceived);

        }

    }

    @Test
    @Order(2)
    @DisplayName("Transmit Object")
    void test_transmitObject() throws Exception {

        //try-with-resources: Mock Client Connector and Server Service w/ Mock Protocol
        try(MockObjectSocket socketClient = new MockObjectSocket();
            ServerService ignored = ServerService.with(TEST_PORT, MockObjectConnectionHandler.class)) {

            //connect to our server
            socketClient.listenToSocket(new Socket(TEST_HOST, TEST_PORT));

            //send the test object
            socketClient.sendObject(new ReturnObjectCommand(TEST_MESSAGE));

            //wait a beat for the async operation to complete
            Thread.sleep(60);

            //assert that the test object was returned by the server (received by the client)
            Assertions.assertEquals(TEST_MESSAGE, socketClient.lastObjectReceived);

        }

    }

}
