import java.time.Instant;

public class Lampada {
    // Estados possíveis da lâmpada
    private enum Estado {
        DESLIGADA, LIGADA, ECO
    }

    // Variáveis de instância
    private Estado estado;
    private double consumoPorMs; // Consumo por milissegundo (em unidades de energia)
    private double consumoTotal; // Consumo total desde a criação da lâmpada
    private double consumoPeriodo; // Consumo desde o último reset
    private Instant ultimoReset; // Data e hora do último reset

    /** Construtor parametrizado */
    public Lampada(double consumoPorMs) {
        this.estado = Estado.DESLIGADA; // Inicialmente, a lâmpada está desligada
        this.consumoPorMs = consumoPorMs; // Consumo por milissegundo fornecido
        this.consumoTotal = 0; // Nenhum consumo até o momento
        this.consumoPeriodo = 0; // Nenhum consumo no período atual
        this.ultimoReset = Instant.now(); // Define o momento do último reset
    }

    /** Ligar a lâmpada no modo de consumo máximo */
    public void lampON() {
        this.estado = Estado.LIGADA;
    }

    /** Desligar a lâmpada */
    public void lampOFF() {
        this.estado = Estado.DESLIGADA;
    }

    /** Ligar a lâmpada no modo ECO */
    public void lampECO() {
        this.estado = Estado.ECO;
    }

    /** Devolver o total de consumo desde a criação da lâmpada */
    public double totalConsumo() {
        return consumoTotal;
    }

    /** Devolver o consumo desde o último reset */
    public double periodoConsumo() {
        return consumoPeriodo;
    }

    /** Método para simular o consumo de energia ao longo do tempo */
    public void passarTempo(long ms) {
        if (estado == Estado.LIGADA) {
            consumoTotal += consumoPorMs * ms;
            consumoPeriodo += consumoPorMs * ms;
        } else if (estado == Estado.ECO) {
            consumoTotal += (consumoPorMs / 2) * ms; // Consumo no modo ECO é metade do consumo máximo
            consumoPeriodo += (consumoPorMs / 2) * ms;
        }
        // No estado DESLIGADA não há consumo
    }

    /** Resetar o consumo do período atual */
    public void resetConsumo() {
        consumoPeriodo = 0;
        ultimoReset = Instant.now();
    }

    /** Retornar o estado atual da lâmpada como uma string */
    public String getEstado() {
        return estado.name();
    }

    // Getters e Setters
    public double getConsumoPorMs() {
        return consumoPorMs;
    }

    public void setConsumoPorMs(double consumoPorMs) {
        this.consumoPorMs = consumoPorMs;
    }
}