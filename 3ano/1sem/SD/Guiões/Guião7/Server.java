import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import static java.util.Arrays.asList;

class ContactManager {

    private HashMap<String, Contact> contacts = new HashMap<>();
    private final ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();

    public void update(Contact c) {
        rwlock.writeLock().lock();
        try {
            contacts.put(c.getName(), c);
            System.out.println("Atualizado: " + c);
        } finally {
            rwlock.writeLock().unlock();
        }
    }

    public ContactList getContactList() {
        rwlock.readLock().lock();
        try {
            ContactList list = new ContactList();
            list.addAll(contacts.values());
            return list;
        } finally {
            rwlock.readLock().unlock();
        }
    }
}

class ServerWorker implements Runnable {

    private Socket socket;
    private ContactManager manager;

    public ServerWorker(Socket socket, ContactManager manager) {
        this.socket = socket;
        this.manager = manager;
    }

    @Override
    public void run() {
        try (
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream())
        ) {
            ContactList list = manager.getContactList();
            list.serialize(out);
            
            while (true) {
                try {
                    Contact c = Contact.deserialize(in);
                    manager.update(c);
                } catch (EOFException e) {
                    // cliente fechou a ligação
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

}

public class Server {

    public static void main (String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        ContactManager manager = new ContactManager();
        // example pre-population
        manager.update(new Contact("John", 20, 253123321, null, asList("john@mail.com")));
        manager.update(new Contact("Alice", 30, 253987654, "CompanyInc.", asList("alice.personal@mail.com", "alice.business@mail.com")));
        manager.update(new Contact("Bob", 40, 253123456, "Comp.Ld", asList("bob@mail.com", "bob.work@mail.com")));

        while (true) {
            Socket socket = serverSocket.accept();
            Thread worker = new Thread(new ServerWorker(socket, manager));
            worker.start();
        }
    }
}