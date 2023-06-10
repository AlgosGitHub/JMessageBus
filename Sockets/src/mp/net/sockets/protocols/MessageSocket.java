package mp.net.sockets.protocols;

import java.io.IOException;

public abstract non-sealed class MessageSocket extends DataStreamSocket {

    Thread listeningThread;

    @Override
    public void startListeningThread() {

        if(listeningThread != null)
            throw new RuntimeException("Listening Thread Already Started");

        listeningThread = new Thread(() -> {

            while (!socket.isClosed()) {

                try {

                    //only read from the stream if there's something to read.
                    if (inputStream.available() != 0)
                        messageReceived(inputStream.readUTF());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }

        });

        listeningThread.start();

    }

    protected abstract void messageReceived(String s);

    public void sendMessage(String message) throws IOException {
        outputStream.writeUTF(message);
        outputStream.flush();
    }

}
