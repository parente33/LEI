/**
 * Classe que representa um Telemóvel, com armazenamento para mensagens,
 * fotos e aplicações, incluindo controlo de espaço e operações básicas
 * sobre as mensagens e aplicações instaladas.
 */
import java.util.ArrayList;

public class Telemovel {
    private String marca;
    private String modelo;
    private int x; // resolução X do display
    private int y; // resolução Y do display
    private int armazenamentoMsgs;
    private int armazenamentoTotal;
    private int armazenamentoFotos;
    private int armazenamentoApps;
    private int espacoOcupado;
    private int numFotos;
    private int numApps;
    private ArrayList<String> nomesApps;
    private ArrayList<String> mensagens;

    /** Construtor por omissão */
    public Telemovel() {
        this.marca = "";
        this.modelo = "";
        this.x = 0;
        this.y = 0;
        this.armazenamentoMsgs = 0;
        this.armazenamentoTotal = 0;
        this.armazenamentoFotos = 0;
        this.armazenamentoApps = 0;
        this.espacoOcupado = 0;
        this.numFotos = 0;
        this.numApps = 0;
        this.nomesApps = new ArrayList<>();
        this.mensagens = new ArrayList<>();
    }

    /** Construtor parametrizado */
    public Telemovel(String marca, String modelo, int x, int y, int armazenamentoMsgs, int armazenamentoTotal, int armazenamentoFotos, int armazenamentoApps) {
        this.marca = marca;
        this.modelo = modelo;
        this.x = x;
        this.y = y;
        this.armazenamentoMsgs = armazenamentoMsgs;
        this.armazenamentoTotal = armazenamentoTotal;
        this.armazenamentoFotos = armazenamentoFotos;
        this.armazenamentoApps = armazenamentoApps;
        this.espacoOcupado = 0;
        this.numFotos = 0;
        this.numApps = 0;
        this.nomesApps = new ArrayList<>();
        this.mensagens = new ArrayList<>();
    }

    /** Construtor de cópia */
    public Telemovel(Telemovel t) {
        this.marca = t.getMarca();
        this.modelo = t.getModelo();
        this.x = t.getX();
        this.y = t.getY();
        this.armazenamentoMsgs = t.getArmazenamentoMsgs();
        this.armazenamentoTotal = t.getArmazenamentoTotal();
        this.armazenamentoFotos = t.getArmazenamentoFotos();
        this.armazenamentoApps = t.getArmazenamentoApps();
        this.espacoOcupado = t.getEspacoOcupado();
        this.numFotos = t.getNumFotos();
        this.numApps = t.getNumApps();
        this.nomesApps = new ArrayList<>(t.getNomesApps());
        this.mensagens = new ArrayList<>(t.getMensagens());
    }

    // Getters e Setters
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getArmazenamentoMsgs() { return armazenamentoMsgs; }
    public void setArmazenamentoMsgs(int armazenamentoMsgs) { this.armazenamentoMsgs = armazenamentoMsgs; }

    public int getArmazenamentoTotal() { return armazenamentoTotal; }
    public void setArmazenamentoTotal(int armazenamentoTotal) { this.armazenamentoTotal = armazenamentoTotal; }

    public int getArmazenamentoFotos() { return armazenamentoFotos; }
    public void setArmazenamentoFotos(int armazenamentoFotos) { this.armazenamentoFotos = armazenamentoFotos; }

    public int getArmazenamentoApps() { return armazenamentoApps; }
    public void setArmazenamentoApps(int armazenamentoApps) { this.armazenamentoApps = armazenamentoApps; }

    public int getEspacoOcupado() { return espacoOcupado; }
    public void setEspacoOcupado(int espacoOcupado) { this.espacoOcupado = espacoOcupado; }

    public int getNumFotos() { return numFotos; }
    public void setNumFotos(int numFotos) { this.numFotos = numFotos; }

    public int getNumApps() { return numApps; }
    public void setNumApps(int numApps) { this.numApps = numApps; }

    public ArrayList<String> getNomesApps() { return nomesApps; }
    public void setNomesApps(ArrayList<String> nomesApps) { this.nomesApps = nomesApps; }

    public ArrayList<String> getMensagens() { return mensagens; }
    public void setMensagens(ArrayList<String> mensagens) { this.mensagens = mensagens; }

    /** Verifica se há espaço suficiente para carregar conteúdo */
    public boolean existeEspaco(int numeroBytes) {
        return (espacoOcupado + numeroBytes) <= armazenamentoTotal;
    }

    /** Instala uma nova aplicação se houver espaço */
    public void instalaApp(String nome, int tamanho) {
        if (existeEspaco(tamanho)) {
            nomesApps.add(nome);
            numApps++;
            espacoOcupado += tamanho;
        }
    }

    /** Recebe e armazena uma nova mensagem de texto */
    public void recebeMsg(String msg) {
        if (mensagens.size() < armazenamentoMsgs) {
            mensagens.add(msg);
        }
    }

    /** Calcula o tamanho médio das aplicações instaladas */
    public double tamMedioApps() {
        if (numApps == 0) return 0.0;
        return (double) armazenamentoApps / numApps;
    }

    /** Devolve a maior mensagem recebida */
    public String maiorMsg() {
        String maior = "";
        for (String msg : mensagens) {
            if (msg.length() > maior.length()) {
                maior = msg;
            }
        }
        return maior;
    }

    /** Remove uma aplicação instalada, libertando espaço */
    public void removeApp(String nome, int tamanho) {
        if (nomesApps.contains(nome)) {
            nomesApps.remove(nome);
            numApps--;
            espacoOcupado -= tamanho;
        }
    }

    /** Devolve a representação textual do objeto */
    public String toString() {
        return "Telemóvel: " + marca + " " + modelo + ", resolução: " + x + "x" + y +
                ", Espaço ocupado: " + espacoOcupado + "/" + armazenamentoTotal;
    }

    /** Verifica igualdade entre objetos */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Telemovel that = (Telemovel) o;
        return this.marca.equals(that.getMarca()) &&
               this.modelo.equals(that.getModelo());
    }

    /** Cria uma cópia do objeto */
    public Telemovel clone() {
        return new Telemovel(this);
    }
}