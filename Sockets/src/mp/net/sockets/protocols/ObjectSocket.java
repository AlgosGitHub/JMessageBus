package mp.net.sockets.protocols;

import mp.net.sockets.Serializer;

import java.io.IOException;
import java.io.Serializable;

public abstract class ObjectSocket extends ByteBlobSocket {

    @Override
    public void bytesReceived(byte[] bytes) {

        try {

            //deserialize the bytes into an object
            Object deserializedObject = Serializer.deserializeObject(bytes);

            //pass that object to our IO handler
            receiveObject(deserializedObject);

        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException("Failed to Receive Object!", ex);
        }

    }

    abstract protected void receiveObject(Object object);

    public void sendObject(Serializable object) {

        try {

            //serialize the object for transmission
            byte[] bytes = Serializer.serializeObject(object);

            //transmit serialized object.
            sendBytes(bytes);

        } catch (IOException e) {
            new RuntimeException("Failed to Send Object!", e);
        }

    }

}
