package org.jbus;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class MessageQueue {

    //todo: reference to backpressure monitor

    private final static BlockingDeque<Message> messageQueue = new LinkedBlockingDeque<>();

    public static void add(Message m) {
        messageQueue.add(m);
        /*
            todo: add MessageBusClientService to parameters so that we can track the origin
                  of the message, and not send it back to itself IF it's listening on the
                  same channel it's broadcasting on.
         */
        //todo: inform the backpressure monitor of our backpressure size
        //System.out.println("Message added to queue. Queue Size is now: " + messageQueue.size());
    }

    public static synchronized Message getNextMessage() throws InterruptedException {
        Message toReturn = messageQueue.take();

        //todo: inform the backpressure monitor of our backpressure size
        //System.out.println("Message polled from queue. Queue Size is now: " + messageQueue.size());
        return toReturn;
    }

    public static List<Message> dump() {
        return messageQueue.stream().toList();
    }

}
