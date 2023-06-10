package mp.net.sockets.tests.mocks;

import mp.net.sockets.tests.commands.ReturnObjectCommand;
import mp.net.sockets.protocols.ObjectSocket;

public class MockObjectConnectionHandler extends ObjectSocket {

    @Override
    protected void receiveObject(Object object) {

        //test-command to return a wrapped object upon receipt.
        if(object instanceof ReturnObjectCommand returnObject) {

            //return the wrapped object
            sendObject(returnObject.object());

        }

    }

}
