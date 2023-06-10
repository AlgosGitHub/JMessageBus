package org.jbus;

import org.jbus.commands.SubscriptionRequest;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageBusClientService extends ObjectSocketRouter {

    private final Set<String> subscriptions = new HashSet<>();

    public static final Set<MessageBusClientService> clients = new HashSet<>();

    {
        clients.add(this);
    }

    public static Set<MessageBusClientService> getSubscribers(String channel) {
        return clients.stream().filter(c -> c.subscribedTo(channel)).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void close() throws IOException {
        super.close();
        clients.remove(this);
    }

    @Override
    protected void receiveObject(Object object) {
        switch(object) {
            case Message m -> MessageQueue.add(m);
            case SubscriptionRequest s -> handleSubscriptionRequest(s);
            default -> System.out.println("Unrecognized object class: " + object.getClass().getName());
        }
    }

    private void handleSubscriptionRequest(SubscriptionRequest s) {
        subscriptions.add(s.channel());
        Undeliverables.retry(s.channel());
    }

    public boolean subscribedTo(String channel) {
        return subscriptions.contains(channel);
    }

    public void pushMessageToClient(Serializable object) {
        sendObject(object);
    }

}
