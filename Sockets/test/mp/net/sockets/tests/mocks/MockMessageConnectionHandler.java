package mp.net.sockets.tests.mocks;

import mp.net.sockets.protocols.MessageSocket;

import java.io.IOException;

public class MockMessageConnectionHandler extends MessageSocket {

    @Override
    protected void messageReceived(String s) {
        try {
            //Send-back whatever we received.
            sendMessage(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
