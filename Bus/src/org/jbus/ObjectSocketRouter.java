package org.jbus;

import mp.net.sockets.protocols.ObjectSocket;

import java.util.HashSet;
import java.util.Set;

/**
 * One-to-Many routing of Objects to ObjectSocketConnectors.
 * Also allows attached ObjectSocketConnectors to sendObjects back up-stream.
 */
public class ObjectSocketRouter extends ObjectSocket {

    private final Set<ObjectSocketConnector> activeConnectors = new HashSet<>();

    public void addConnector(ObjectSocketConnector objectSocketConnector) {
        activeConnectors.add(objectSocketConnector);
    }

    @Override
    protected void receiveObject(Object object) {

        //fanout the object to all connected ObjectSocketConnectors
        for(ObjectSocketConnector handlerProtocol : activeConnectors)
            handlerProtocol.receiveObject(object);

    }

}