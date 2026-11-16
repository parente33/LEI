import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
    static class ServerWorker implements Runnable {
        private final Socket socket;

        public ServerWorker(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                String line;
                int soma = 0;
                int count = 0;

                while ((line = in.readLine()) != null) {
                    try {
                        int recebido = Integer.parseInt(line);
                        soma += recebido;
                        count++;
                        out.println("Soma atual: " + soma);
                    } catch (NumberFormatException e) {
                        out.println("Erro: entrada inválida");
                    }
                }
                if (count > 0) {
                    float average = (float) soma / count;
                    out.println("Média final: " + average);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        try (ServerSocket ss = new ServerSocket(12345)) {
            System.out.println("Servidor à escuta na porta 12345...");

            while (true) {
                Socket socket = ss.accept();

                Thread workerThread = new Thread(new ServerWorker(socket));
                workerThread.run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
