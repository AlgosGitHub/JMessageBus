package org.jbus;

import java.util.*;

public final class Undeliverables {

    private final static Map<String, Deque<Message>> undeliveredMessages = new HashMap<>();

    public static synchronized void add(Message m) {
        undeliveredMessages.computeIfAbsent(m.channel(), c -> new ArrayDeque<>()).add(m);
    }

    public static synchronized Deque<Message> get(String channel) {

        if(undeliveredMessages.containsKey(channel))
            return undeliveredMessages.remove(channel);

        return new ArrayDeque<>();

    }

    public static void retry(String channel) {

        //remove all queued messages for this channel, if any
        Deque<Message> messages = get(channel);

        //add all those messages back into the message queue for delivery.
        messages.forEach(MessageQueue::add);

    }

    public static Map<String, Deque<Message>> dump() {
        return Collections.unmodifiableMap(undeliveredMessages);
    }

}
