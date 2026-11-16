package uminho.sd.protocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TaggedConnection wraps a TCP socket to provide thread-safe send/receive of Frames.
 * Multiple threads can safely call send() concurrently.
 */
public class TaggedConnection implements AutoCloseable {

    private final Socket socket;

    private final DataInputStream  in;
    private final DataOutputStream out;

    private final ReentrantLock sendLock;

    /**
     * Creates a TaggedConnection from an existing socket.
     *
     * @param socket Connected TCP socket
     */
    public TaggedConnection(Socket socket) throws IOException {
        this.socket = socket;

        this.in = new DataInputStream(
            new BufferedInputStream(socket.getInputStream())
        );
        this.out = new DataOutputStream(
            new BufferedOutputStream(socket.getOutputStream())
        );

        this.sendLock = new ReentrantLock();
    }

    /**
     * Thread-safe method to send a frame.
     *
     * @param frame Frame to send
     */
    public void send(Frame frame) throws IOException {
        sendLock.lock();
        try {
            frame.send(out);
        } finally {
            sendLock.unlock();
        }
    }

    /**
     * Blocking receive of a frame.
     * Should only be called by one thread (the demultiplexer receiver thread).
     *
     * @return Received frame
     */
    public Frame receive() throws IOException {
        return Frame.receive(in);
    }

    /**
     * Closes the underlying socket.
     */
    @Override
    public void close() throws IOException {
        socket.close();
    }

    /**
     * Returns the underlying socket.
     *
     * @return The socket
     */
    public Socket getSocket() {
        return socket;
    }
}
