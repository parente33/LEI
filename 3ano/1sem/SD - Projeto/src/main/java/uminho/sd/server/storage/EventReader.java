package uminho.sd.server.storage;

import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

import uminho.sd.protocol.SalesEvent;

/**
 * Reads SalesEvents from a file using DataInputStream.
 */
public class EventReader implements Iterator<SalesEvent>, AutoCloseable {

    private final DataInputStream in;

    private SalesEvent nextEvent;
    private boolean    done;

    public EventReader(File file) throws IOException {
        this.in = new DataInputStream(
            new BufferedInputStream(new FileInputStream(file))
        );

        this.nextEvent = null;
        this.done      = false;
    }

    private void advance() {
        if (nextEvent != null || done) {
            return;
        }

        try {
            String product = in.readUTF();
            int quantity   = in.readInt();
            double price   = in.readDouble();

            nextEvent = new SalesEvent(product, quantity, price);
        } catch (EOFException e) {
            done = true; close();
        } catch (IOException e) {
            done = true; close();
        }
    }

    @Override
    public boolean hasNext() {
        advance();
        return nextEvent != null;
    }

    @Override
    public SalesEvent next() {
        advance();
        if (nextEvent == null) {
            throw new NoSuchElementException();
        }
        SalesEvent e = nextEvent;
        nextEvent = null;
        return e;
    }

    @Override
    public void close() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                /* Ignore close errors */
            }
        }
    }
}
