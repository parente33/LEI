import java.time.LocalDate;

public class Carro {
    private String matricula;
    private String marca;
    private String modelo;
    private LocalDate anoconst;
    private double velmed;
    private double auton;
    private double kmtstotais;

    public Carro() {
        this.matricula = "";
        this.marca = "";
        this.modelo = "";
        this.anoconst = LocalDate.now();
        this.velmed = 0.0;
        this.auton = 0.0;
        this.kmtstotais = 0.0;
    }

    public Carro(String matricula, String marca, String modelo, LocalDate anoconst, double velmed, double auton, double kmtstotais) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.anoconst = anoconst;
        this.velmed = velmed;
        this.auton = auton;
        this.kmtstotais = kmtstotais;
    }

    public Carro(Carro umCarro) {
        this.matricula = umCarro.getMatricula();
        this.marca = umCarro.getMarca();
        this.modelo = umCarro.getModelo();
        this.anoconst = umCarro.getAnoConst();
        this.velmed = umCarro.getVelMed();
        this.auton = umCarro.getAuton();
        this.kmtstotais = umCarro.getKmsTotais();
    }

    public String getMatricula() { return this.matricula; }
    public String getMarca() { return this.marca; }
    public String getModelo() { return this.modelo; }
    public LocalDate getAnoConst() { return this.anoconst; }
    public double getVelMed() { return this.velmed; }
    public double getAuton() { return this.auton; }
    public double getKmsTotais() { return this.kmtstotais; }

    public void setMatricula(String matricula) { this.matricula = matricula; }
    public void setMarca(String marca) { this.marca = marca; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public void setAnoConst(LocalDate anoconst) { this.anoconst = anoconst; }
    public void setVelMed(double velmed) { this.velmed = velmed; }
    public void setAuton(double auton) { this.auton = auton; }
    public void setKmsTotais(double kmtstotais) { this.kmtstotais = kmtstotais; }

    public void registarViagem(double kms) {
        if (kms <= this.auton) {
            this.auton -= kms;
            this.kmtstotais += kms;
        } else {
            throw new IllegalArgumentException("Autonomia insuficiente para a viagem.");
        }
    }

    public void resetarKms() {
        this.kmtstotais = 0;
    }

    public double custoPorKm() {
        return 0; // Implementado nas subclasses
    }

    public double custoRealPorKm() {
        return custoPorKm() * 1.15;
    }

    public void abastecerOuCarregar() {
        // Implementado nas subclasses
    }
}