package mp.net.sockets.protocols;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class ByteBlobSocket extends MessageSocket {

    boolean processMessages = true;

    private Thread listeningThread;

    private Thread processingThread;

    private final BlockingQueue<byte[]> messageQueue = new LinkedBlockingQueue<>();

    private final static String INCOMING_BYTES = "INCOMING_BYTES";

    public abstract void bytesReceived(byte[] bytes);

    protected void sendBytes(byte[] bytes) throws IOException {

        sendMessage(INCOMING_BYTES +":"+bytes.length);

        outputStream.write(bytes);
        outputStream.flush();

    }

    @Override
    public void startListeningThread() {

        if(listeningThread != null)
            throw new RuntimeException("Listening Thread Already Started");

        if(processingThread != null)
            throw new RuntimeException("Message Processing Thread Already Started");

        //
        listeningThread = new Thread(() -> {

            try {

                boolean awaitingByteBlob = false;
                int incomingBytes = 0;

                while(!socket.isClosed()) {

                    //if we're awaiting a byte blob, assume this is it.
                    if (awaitingByteBlob) {
                        awaitingByteBlob = false;

                        byte[] bytes = inputStream.readNBytes(incomingBytes);

                        // put the message into a buffer and process it asap! don't block traffic.
                        messageQueue.add(bytes);

                    } else { // if we're not waiting for a byte blob, assume it's just another UTF String.

                        String message = inputStream.readUTF();

                        if (message.startsWith(INCOMING_BYTES)) {
                            incomingBytes = Integer.parseInt(message.substring(message.indexOf(":") + 1));
                            awaitingByteBlob = true;
                        }

                    }

                }

            } catch (EOFException e) {
                System.out.println("Socket Disconnected.");
                //question: should we do anything here?
            } catch (SocketException e) {
                if(e.getMessage().equals("Socket closed"))
                    System.out.println("Socket Closed by Thy Own Hand");
                else throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

        //
        processingThread = new Thread(() -> {

            while(processMessages) {

                try {

                    // poll the queue
                    byte[] receivedByteBlob = messageQueue.take();

                    // process item
                    bytesReceived(receivedByteBlob);

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            }

        });

        // fire it up
        listeningThread.start();
        processingThread.start();

    }

    @Override
    protected void messageReceived(String s) {
        throw new RuntimeException("ByteBlobIO.messageReceived was called. This should never happen.");
    }

}
