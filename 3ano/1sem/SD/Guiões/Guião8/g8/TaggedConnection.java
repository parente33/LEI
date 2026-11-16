package g8;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TaggedConnection implements AutoCloseable {

    public static class Frame {
        public final int tag;
        public final byte[] data;

        public Frame(int tag, byte[] data) {
            this.tag = tag; this.data = data;
        }
    }

    private final FramedConnection conn;
    private final ByteArrayOutputStream buf = new ByteArrayOutputStream();
    private final DataOutputStream dataOut = new DataOutputStream(buf);

    public TaggedConnection(Socket socket) throws IOException {
        this.conn = new FramedConnection(socket);
    }

    public void send(Frame frame) throws IOException {
        send(frame.tag, frame.data);
    }

    public void send(int tag, byte[] data) throws IOException {
        buf.reset();
        dataOut.writeInt(tag);
        dataOut.writeInt(data.length);
        dataOut.write(data);
        dataOut.flush();
        conn.send(buf.toByteArray());
    }

    public Frame receive() throws IOException {
        byte[] raw = conn.receive();
        DataInputStream din = new DataInputStream(new ByteArrayInputStream(raw));
        int tag = din.readInt();
        int len = din.readInt();
        byte[] payload = new byte[len];
        din.readFully(payload);
        return new Frame(tag, payload);
    }

    @Override
    public void close() throws IOException {
        conn.close();
    }
}