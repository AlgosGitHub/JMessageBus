package org.jbus;

import java.util.Set;

public class DeliveryThread {

    boolean carryOn = true;

    public DeliveryThread() {
        new Thread(() -> {
            while(carryOn) {

                try {

                    Message m = MessageQueue.getNextMessage();

                    dispatchMessage(m);

                } catch (InterruptedException ex) {
                    System.out.println("Delivery Thread Failure: Message Queue Polling Exception. Shutting down.");
                    stop();
                }

            }
        }).start();
    }

    public void stop() {
        carryOn = false;
    }

    private void dispatchMessage(Message m) {

        // get all channel subscribers
        Set<MessageBusClientService> subscribers = MessageBusClientService.getSubscribers(m.channel());

        // if none, retain the message
        if(subscribers.isEmpty()) {
            Undeliverables.add(m);
            return;
        }

        // otherwise, push message
        subscribers.forEach(sub -> sub.pushMessageToClient(m));

    }

}
