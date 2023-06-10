package mp.net.sockets.tests.mocks;

import mp.net.sockets.protocols.MessageSocket;

public class MockMessageSocket extends MessageSocket {
    public String lastMessageReceived;

    @Override
    protected void messageReceived(String message) {
        this.lastMessageReceived = message;
    }

}
