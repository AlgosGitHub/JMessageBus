package mp.net.sockets.protocols;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Abstraction of a socket input/output device.
 * Closeable because it takes responsibility for closing the socket it receives.
 */
public abstract sealed class DataStreamSocket implements Closeable permits MessageSocket {

    protected Socket socket;
    protected DataOutputStream outputStream;
    protected DataInputStream inputStream;

    protected abstract void startListeningThread();

    @Override
    public void close() throws IOException {

        if(socket != null)
            socket.close();

    }

    public void listenToSocket(Socket socket) throws IOException {

        if(this.socket != null)
            throw new RuntimeException("Socket already set!");

        this.socket = socket;
        this.outputStream  =  new DataOutputStream(socket.getOutputStream());
        this.inputStream   =  new DataInputStream(socket.getInputStream());

        startListeningThread();

    }

}
