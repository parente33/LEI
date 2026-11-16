package uminho.sd.protocol;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple tests to verify Frame serialization/deserialization
 */
public class FrameTest {

    @Test
    void serializeAndDeserializeFrame() throws IOException {
        byte[] payload = "Hello, World!".getBytes();
        Frame original = new Frame(67, Frame.LOGIN, payload);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream      dos  = new DataOutputStream(baos);
        original.send(dos);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DataInputStream      dis  = new DataInputStream(bais);
        Frame received = Frame.receive(dis);

        assertEquals(original.getTag(), received.getTag(), "Tag mismatch");
        assertEquals(original.getType(), received.getType(), "Type mismatch");
        assertArrayEquals(original.getPayload(), received.getPayload(), "Payload mismatch");
    }

    @Test
    void serializeAndDeserializeEmptyPayload() throws IOException {
        Frame emptyFrame = new Frame(1, Frame.HEARTBEAT, Serialization.serializeVoid());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream      dos  = new DataOutputStream(baos);
        emptyFrame.send(dos);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DataInputStream      dis  = new DataInputStream(bais);
        Frame receivedEmpty = Frame.receive(dis);

        assertEquals(0, receivedEmpty.getPayload().length, "Empty payload length mismatch");
    }
}
