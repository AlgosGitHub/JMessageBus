package org.jbus.remote;

import org.jbus.JMBClient;
import org.jbus.JMessageBus;
import org.junit.jupiter.api.*;

@DisplayName("JRemoteMethodCaller")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestRemoteMethodCaller {

    private final String TEST_CHANNEL = "hello.world", TEST_MESSAGE = "Hello, World.";

    private final int TEST_PORT = 777;

    JMessageBus messageBus = JMessageBus.start(TEST_PORT);

    JMBClient jmbClient = JMBClient.connect("localhost", TEST_PORT);

    PortHost portHost;

    public TestRemoteMethodCaller() throws Exception {}

    @Test
    @Order(1)
    @DisplayName("Host a Port")
    public void hostPort() throws Exception {

        Assertions.assertDoesNotThrow(() -> {
            portHost = PortHost.with(jmbClient, TEST_CHANNEL, TestPort.class, () -> TEST_MESSAGE);
        });

    }

    @Test
    @Order(2)
    @DisplayName("Call Remote Method")
    public void callRemoteMethod() throws Exception {

        TestPort remoteMethodCaller = RemoteMethodCaller.wrap(TestPort.class, jmbClient, TEST_CHANNEL);

        String value = remoteMethodCaller.getTestMessage();

        Assertions.assertEquals(TEST_MESSAGE, value);

    }

    @Test
    @Order(3)
    @DisplayName("Shutdown")
    public void shutdown() throws Exception {

        Assertions.assertDoesNotThrow(() -> {
            jmbClient.disconnect();
            messageBus.shutdown();
        });

    }


}
