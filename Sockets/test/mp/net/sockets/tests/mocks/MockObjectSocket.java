package mp.net.sockets.tests.mocks;

import mp.net.sockets.protocols.ObjectSocket;

public class MockObjectSocket extends ObjectSocket {

    public Object lastObjectReceived;

    @Override
    protected void receiveObject(Object object) {
        lastObjectReceived = object;
    }

}
