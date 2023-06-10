package org.jbus;

import org.junit.jupiter.api.*;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@DisplayName("JMessageBus")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JMessageBusTests {

    private final String
        TEST_CHANNEL = "test.channel",
        TEST_MESSAGE = "Hello World!";

    record TestObjectType(String value) implements Serializable {};

    private final TestObjectType TEST_OBJECT = new TestObjectType(TEST_MESSAGE);

    private JMBClient jmbClient;

    private JMessageBus messageBus;

    @Test
    @Order(1)
    @DisplayName("Start Message Bus")
    public void startMessageBus() throws Exception {

        Assertions.assertDoesNotThrow(() -> {
            messageBus = JMessageBus.start(7777);
        });

    }

    @Test
    @Order(2)
    @DisplayName("Connect to Message Bus")
    public void connectToBus() throws Exception {

        Assertions.assertDoesNotThrow(() -> {
            jmbClient = JMBClient.connect("localhost", 7777);
        });

    }

    @Test
    @Order(3)
    @DisplayName("Push & Subscribe to Messages")
    public void subToMessages() throws Exception {

        jmbClient.pushMessage(TEST_CHANNEL, TEST_OBJECT);

        Thread.sleep(1000);

        CompletableFuture<TestObjectType> helloFuture = new CompletableFuture();

        Consumer<Serializable> messageConsumer = serializable -> {

            if(serializable instanceof TestObjectType tot)
                helloFuture.complete(tot);

        };

        jmbClient.subscribe(TEST_CHANNEL, messageConsumer);

        TestObjectType backFromTheFuture = helloFuture.get(3, TimeUnit.SECONDS);

        Assertions.assertEquals(TEST_MESSAGE, backFromTheFuture.value);

    }


    @Test
    @Order(6)
    @DisplayName("Disconnect from Bus")
    public void disconnect() throws Exception {

        Assertions.assertDoesNotThrow(() -> {
            jmbClient.disconnect();
        });

    }

    @Test
    @Order(7)
    @DisplayName("Shutdown Message Bus")
    public void shutdownBus() throws Exception {

        Assertions.assertDoesNotThrow(() -> {
            messageBus.shutdown();
        });

    }

}
