import java.time.LocalDateTime;

public class SmartDevice implements Comparable<SmartDevice> {
    private String id;
    private boolean on;
    private double consumoPorHora;
    private LocalDateTime inicio;

    public SmartDevice(String id, double consumoPorHora) {
        this.id = id;
        this.on = false;
        this.consumoPorHora = consumoPorHora;
    }

    // devolve o consumo desde o início
    public double totalConsumo() {}

    //liga o device. Se for a primeira vez inicializa o tempo de inicio
    public void turnOn() {
        this.on = true;
        if (this.inicio == null)
            this.inicio = LocalDateTime.now();
    }

    @Override
    public int compareTo(SmartDevice outro) {
        return Double.compare(this.consumoPorHora,outro.consumoPorHora);
    }

    @Override
    public SmartDevice clone() {
        return new SmartDevice(this.id,this.consumoPorHora);
    }
}