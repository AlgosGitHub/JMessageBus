package org.jbus;

import mp.net.sockets.protocols.ObjectSocket;

/**
 * This is a generic ObjectSocket Adapter.
 * Extend it to make use of the Objects received via Socket.
 * For example, to bridge Object Flow into a PortConnector, MessageBus,
 * or simply to split the object flow into multiple listeners.
 */
public abstract class ObjectSocketConnector {

    /**
     * We'll need this to respond to commands.
     */
    protected ObjectSocket objectSocket;

    public ObjectSocketConnector(ObjectSocket objectSocket) {
        this.objectSocket = objectSocket;
    }

    public abstract void receiveObject(Object object);

    public abstract void disconnect();

}
