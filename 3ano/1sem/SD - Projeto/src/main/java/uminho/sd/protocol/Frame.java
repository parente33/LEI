package uminho.sd.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Frame represents the wire protocol format for request/response communication.
 *
 * ┌──────────┬─────────┬──────────┬─────────────┐
 * │ Tag      │ Type    │ Length   │ Payload     │
 * ├──────────┼─────────┼──────────┼─────────────┤
 * │ 4 bytes  │ 1 byte  │ 4 bytes  │ Variable    │
 * └──────────┴─────────┴──────────┴─────────────┘
 */
public class Frame {
    public static final int MAX_PAYLOAD_SIZE = 1 * 1024 * 1024;

    /* Request/Response Type Constants */
    public static final byte REGISTER = 1;
    public static final byte LOGIN = 2;
    public static final byte ADD_EVENT = 3;
    public static final byte QUERY_SALES_QUANTITY = 4;
    public static final byte QUERY_SALES_VOLUME = 5;
    public static final byte QUERY_AVG_PRICE = 6;
    public static final byte QUERY_MAX_PRICE = 7;
    public static final byte FILTER_EVENTS = 8;
    public static final byte NOTIFY_SIMULTANEOUS = 9;
    public static final byte NOTIFY_CONSECUTIVE = 10;
    public static final byte START_NEW_DAY = 11;
    public static final byte HEARTBEAT = 12;

    /* Response type constants */
    public static final byte RESPONSE_OK = 50;
    public static final byte RESPONSE_ERROR = 51;

    /* Fields */
    private final int    tag;
    private final byte   type;
    private final byte[] payload;

    /**
     * Constructs a new Frame
     *
     * @param tag     Unique identifier to match request/response
     * @param type    Request or response type
     * @param payload Serialized data (can be empty byte array)
     */
    public Frame(int tag, byte type, byte[] payload) {
        this.tag     = tag;
        this.type    = type;
        this.payload = payload != null ? payload : Serialization.serializeVoid();
    }

    /**
     * Serialize this frame to a DataOutputStream
     *
     * @param out Output stream to write to
     */
    public void send(DataOutputStream out) throws IOException {
        out.writeInt(tag);
        out.writeByte(type);
        out.writeInt(payload.length);
        out.write(payload);
        out.flush();
    }

    /**
     * Deserializes a frame from the given input stream.
     * Blocks until a complete frame is read.
     *
     * @param in Input stream
     * @return The received Frame
     */
    public static Frame receive(DataInputStream in) throws IOException {
        int  tag    = in.readInt();
        byte type   = in.readByte();
        int  length = in.readInt();

        if (length < 0 || length > MAX_PAYLOAD_SIZE) {
            throw new IOException("Invalid payload length: " + length);
        }

        byte[] payload = new byte[length];
        if (length > 0) {
            in.readFully(payload);
        }

        return new Frame(tag, type, payload);
    }

    /**
     * Returns the tag value of the frame.
     *
     * @return the tag as an integer
     */
    public int getTag() {
        return tag;
    }

    /**
     * Returns the type of the frame.
     *
     * @return the type as a byte
     */
    public byte getType() {
        return type;
    }

    /**
     * Returns the payload.
     * Warning: Returns average mutable array. Clients should not modify it.
     */
    public byte[] getPayload() {
        return payload;
    }
}
