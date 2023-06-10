package mp.net.sockets.tests.mocks;

import mp.net.sockets.protocols.ObjectSocket;

import java.io.Serializable;

public class InertObjectSocketAdapter extends ObjectSocket {

    public Object lastObjectReceived;

    @Override
    protected void receiveObject(Object object) {
        lastObjectReceived = object;
    }

    public Object lastObjectSent;

    @Override
    public void sendObject(Serializable object) {
        lastObjectSent = object;
    }

}
