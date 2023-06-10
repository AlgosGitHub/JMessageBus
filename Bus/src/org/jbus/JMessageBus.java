package org.jbus;

import mp.net.sockets.ServerService;

import java.io.IOException;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class JMessageBus {

    //todo: Threadpool of Delivery Threads
    DeliveryThread deliveryThread = new DeliveryThread();

    //todo: backpressure monitor

    private final ServerService serverService;
    JMessageBus(int port) throws Exception {
        this.serverService = ServerService.with(port, MessageBusClientService.class);
    }

    public static JMessageBus start(int port) throws Exception {
        return new JMessageBus(port);
    }

    public void shutdown() throws IOException {

        // stop accepting new connections
        serverService.close();

        // stop sending messages
        deliveryThread.stop();

        //todo: dump what's left in the titular message queue
        List<Message> leftoverMessages = MessageQueue.dump();

        //todo: save all remaining messages in queue to "Undeliverables" retainer.
        Map<String, Deque<Message>> dump = Undeliverables.dump();

    }

}
