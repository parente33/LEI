package g8;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FramedConnection implements AutoCloseable {

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    private final Lock readLock;
    private final Lock writeLock;

    public FramedConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.in  = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.readLock = new ReentrantLock();
        this.writeLock = new ReentrantLock();
    }

    public void send(byte[] data) throws IOException {
        writeLock.lock();
        try {
            out.writeInt(data.length);
            out.write(data);
            out.flush();
        } finally {
            writeLock.unlock();
        }
    }

    public byte[] receive() throws IOException {
        readLock.lock();
        try {
            int size = in.readInt();
            byte[] buf = new byte[size];
            in.readFully(buf);
            return buf;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}