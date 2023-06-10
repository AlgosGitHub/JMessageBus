package mp.net.sockets;

import mp.net.sockets.protocols.DataStreamSocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerService implements AutoCloseable {

    private final ServerSocket serverSocket;

    private final Class<? extends DataStreamSocket> connectionHandlerClass;

    private ServerService(int port, Class<? extends DataStreamSocket> connectionHandlerClass) throws IOException {

        this.serverSocket = new ServerSocket(port);
        this.connectionHandlerClass = connectionHandlerClass;

        new Thread(this::listenForIncomingConnections).start();

    }

    private void listenForIncomingConnections() {

        while (!serverSocket.isClosed()) {

            try {

                // accept incoming connection (blocks while waiting)
                Socket connectionToClient = serverSocket.accept();

                // spawn a virtual thread to handle communication with our new friend
                new Thread(() -> handleIncomingConnection(connectionToClient)).start();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    }

    public void handleIncomingConnection(Socket connection) {

        try {

            // instantiate our Socket I/O handler.
            DataStreamSocket socketIO = connectionHandlerClass.getDeclaredConstructor().newInstance();

            // bind the IO handler to the connection
            socketIO.listenToSocket(connection);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static ServerService with(int port, Class<? extends DataStreamSocket> dataHandlerClass) throws Exception {
        return new ServerService(port, dataHandlerClass);
    }

    /**
     * Safely disengages ServerSocket.accept() by providing it a fail-safe connection to disengage the blocking mechanism.
     * Without this, ServerSocket.close() will throw an error.
     */
    private void eatLastConnection() throws IOException {

        // attempt a connection to the listener to ensure ServerSocket.accept() can stop blocking before we close the server.
        new Socket("localhost", serverSocket.getLocalPort()).close();

    }

    @Override
    public void close() throws IOException {

        // safely disengage ServerSocket.accept()
        eatLastConnection();

        // finally, shutdown the socket
        serverSocket.close();

    }

}
