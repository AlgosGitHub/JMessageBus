package org.jbus;

import mp.net.sockets.protocols.ObjectSocket;
import org.jbus.commands.SubscriptionRequest;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class JMBClient extends ObjectSocket {

    Map<String, Set<Consumer<Serializable>>> channelConsumers = new HashMap<>();

    JMBClient(String hostAddress, int port) throws IOException {
        listenToSocket(new Socket(hostAddress, port));
    }

    public static JMBClient connect(String hostAddress, int port) throws IOException {
        return new JMBClient(hostAddress, port);
    }

    public void pushMessage(String channel, Serializable object) {
        sendObject(new Message(channel, object));
    }

    public void subscribe(String channel, Consumer<Serializable> messageConsumer) throws Exception {

        // retain our channel message listener
        channelConsumers.computeIfAbsent(channel, c -> new HashSet<>()).add(messageConsumer);

        // subscribe to this channel
        sendObject(new SubscriptionRequest(channel));

    }

    public void disconnect() throws IOException {
        close();
    }

    @Override
    protected void receiveObject(Object message) {

        Thread.startVirtualThread(() -> {

            if(message instanceof Message m) handleMessage(m);
            else System.out.println("Unrecognized message type: " + message.getClass().getName());

        });

    }

    private void handleMessage(Message m) {

        if(!channelConsumers.containsKey(m.channel()))
            return;

        Set<Consumer<Serializable>> consumers = channelConsumers.get(m.channel());

        consumers.forEach(c -> c.accept(m.contents()));

    }

}
