import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Client {

    public static Contact parseLine(String userInput) {
        String[] tokens = userInput.split(" ");

        String name = tokens[0];
        int age = Integer.parseInt(tokens[1]);
        long phone = Long.parseLong(tokens[2]);

        String company = tokens[3].equals("null") ? null : tokens[3];

        ArrayList<String> emails = new ArrayList<>(Arrays.asList(tokens).subList(4, tokens.length));

        return new Contact(name, age, phone, company, emails);
    }

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345);

        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        ContactList list = ContactList.deserialize(in);
        System.out.println("Contactos existentes:");
        for (Contact c : list) {
            System.out.println(c);
        }

        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        String line;
        while ((line = stdin.readLine()) != null) {
            Contact c = parseLine(line);
            c.serialize(out);
        }

        socket.close();
    }
}