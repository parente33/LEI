import java.time.LocalDate;

public class CarroEletrico extends Carro {
    private double dimBateria; // em kWh
    private double consumoBateria; // aos 100km
    private double precoKW;

    public CarroEletrico() {
        super();
        this.dimBateria = 0.0;
        this.consumoBateria = 0.0;
        this.precoKW = 0.0;
    }

    public CarroEletrico(String matricula, String marca, String modelo, LocalDate anoconst, double velmed, double auton, double kmtstotais,
                         double dimBateria, double consumoBateria, double precoKW) {
        super(matricula,marca,modelo,anoconst,velmed,auton,kmtstotais);
        this.dimBateria = dimBateria;
        this.consumoBateria = consumoBateria;
        this.precoKW = precoKW;
    }

    public CarroEletrico(CarroEletrico umCarro) {
        super(umCarro);
        this.dimBateria = umCarro.getDimensaoBateria();
        this.consumoBateria = umCarro.getConsumoKWh100Km();
        this.precoKW = umCarro.getPrecoKWh();
    }

    @Override
    public double custoPorKm() {
        return (consumoBateria / 100) * precoKW;
    }

    @Override
    public void abastecerOuCarregar() {
        double novaAutonomia = (dimBateria / (consumoBateria / 100));
        setAuton(novaAutonomia);
    }

    public double getDimensaoBateria() { return dimBateria; }
    public double getConsumoKWh100Km() { return consumoBateria; }
    public double getPrecoKWh() { return precoKW; }

    public void setDimensaoBateria(double dimBateria) { this.dimBateria = dimBateria; }
    public void setConsumoKWh100Km(double consumoBateria) { this.consumoBateria = consumoBateria; }
    public void setPrecoKW(double precoKW) { this.precoKW = precoKW; }
}