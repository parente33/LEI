public class Carro {
    // Variáveis de instância
    private String marca;
    private String modelo;
    private int anoConstrucao;
    private double consumoPorKm; // Consumo por km a 100 km/h
    private double kmsTotais;
    private double mediaConsumo;
    private double kmsUltimaViagem;
    private double mediaConsumoUltimaViagem;
    private double regeneracaoEnergia; // Capacidade de regeneração de energia por km
    private boolean ligado; // Indica se o carro está ligado ou não

    /** Construtor da classe */
    public Carro(String marca, String modelo, int anoConstrucao, double consumoPorKm, double regeneracaoEnergia) {
        this.marca = marca;
        this.modelo = modelo;
        this.anoConstrucao = anoConstrucao;
        this.consumoPorKm = consumoPorKm;
        this.kmsTotais = 0;
        this.mediaConsumo = 0;
        this.kmsUltimaViagem = 0;
        this.mediaConsumoUltimaViagem = 0;
        this.regeneracaoEnergia = regeneracaoEnergia;
        this.ligado = false; // O carro começa desligado
    }

    // Métodos de acesso e alteração
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getAnoConstrucao() { return anoConstrucao; }
    public void setAnoConstrucao(int anoConstrucao) { this.anoConstrucao = anoConstrucao; }

    public double getConsumoPorKm() { return consumoPorKm; }
    public void setConsumoPorKm(double consumoPorKm) { this.consumoPorKm = consumoPorKm; }

    public double getKmsTotais() { return kmsTotais; }
    public void setKmsTotais(double kmsTotais) { this.kmsTotais = kmsTotais; }

    public double getMediaConsumo() { return mediaConsumo; }
    public void setMediaConsumo(double mediaConsumo) { this.mediaConsumo = mediaConsumo; }

    public double getKmsUltimaViagem() { return kmsUltimaViagem; }
    public void setKmsUltimaViagem(double kmsUltimaViagem) { this.kmsUltimaViagem = kmsUltimaViagem; }

    public double getMediaConsumoUltimaViagem() { return mediaConsumoUltimaViagem; }
    public void setMediaConsumoUltimaViagem(double mediaConsumoUltimaViagem) { this.mediaConsumoUltimaViagem = mediaConsumoUltimaViagem; }

    public double getRegeneracaoEnergia() { return regeneracaoEnergia; }
    public void setRegeneracaoEnergia(double regeneracaoEnergia) { this.regeneracaoEnergia = regeneracaoEnergia; }

    public boolean isLigado() { return ligado; }
    public void setLigado(boolean ligado) { this.ligado = ligado; }

    /** Método que liga o carro */
    public void ligaCarro() {
        if (!ligado) {
            ligado = true;
        }
    }

    /** Método que desliga o carro */
    public void desligaCarro() {
        if (ligado) {
            ligado = false;
            resetUltimaViagem(); // Quando desliga, o contador de última viagem é resetado
        }
    }

    /** Método que faz um reset explícito do contador de última viagem */
    public void resetUltimaViagem() {
        this.kmsUltimaViagem = 0;
        this.mediaConsumoUltimaViagem = 0;
    }

    /** Método que avança o carro X metros a uma velocidade V km/h */
    public void avancaCarro(double metros, double velocidade) {
        if (!ligado) {
            System.out.println("O carro precisa estar ligado para andar.");
            return;
        }
        
        // Converte metros para quilômetros
        double kms = metros / 1000;

        // Calcula o consumo de combustível
        double consumo = (consumoPorKm * kms) * (velocidade / 100.0);
        
        // Atualiza os dados
        kmsTotais += kms;
        kmsUltimaViagem += kms;

        // Atualiza a média de consumo
        mediaConsumo = (mediaConsumo * (kmsTotais - kms) + consumo) / kmsTotais;
        mediaConsumoUltimaViagem = (mediaConsumoUltimaViagem * (kmsUltimaViagem - kms) + consumo) / kmsUltimaViagem;
    }

    /** Método que faz o carro travar durante X metros */
    public void travaCarro(double metros) {
        if (!ligado) {
            System.out.println("O carro precisa estar ligado para travar.");
            return;
        }

        // Converte metros para quilômetros
        double kms = metros / 1000;

        // O consumo é reduzido devido à regeneração de energia
        double consumoReduzido = (consumoPorKm * kms) - (regeneracaoEnergia * kms);

        // Atualiza os dados
        kmsTotais += kms;
        kmsUltimaViagem += kms;

        // Atualiza a média de consumo
        mediaConsumo = (mediaConsumo * (kmsTotais - kms) + consumoReduzido) / kmsTotais;
        mediaConsumoUltimaViagem = (mediaConsumoUltimaViagem * (kmsUltimaViagem - kms) + consumoReduzido) / kmsUltimaViagem;
    }
}