import java.time.LocalDate;

public class CarroCombustao extends Carro {
    private double tamDeposito; // em litros
    private double consumo; // aos 100km
    private double precoLitro; // preço da gota

    public CarroCombustao() {
        super();
        this.tamDeposito = 0.0;
        this.consumo = 0.0;
        this.precoLitro = 0.0;
    }

    public CarroCombustao(String matricula, String marca, String modelo, LocalDate anoconst, double velmed, double auton, double kmtstotais,
                         double tamDeposito, double consumo, double precoLitro) {
        super(matricula,marca,modelo,anoconst,velmed,auton,kmtstotais);
        this.tamDeposito = tamDeposito;
        this.consumo = consumo;
        this.precoLitro = precoLitro;
    }

    public CarroCombustao(CarroCombustao umCarro) {
        super(umCarro);
        this.tamDeposito = umCarro.getTamanhoDeposito();
        this.consumo = umCarro.getConsumoLitros100Km();
        this.precoLitro = umCarro.getPrecoLitro();
    }

    @Override
    public double custoPorKm() {
        return ((this.consumo/100) * this.precoLitro);
    }

    @Override
    public void abastecerOuCarregar() {
        double novaAutonomia = (tamDeposito / (consumo / 100));
        setAuton(novaAutonomia);
    }

    public double getTamanhoDeposito() { return tamDeposito; }
    public double getConsumoLitros100Km() { return consumo; }
    public double getPrecoLitro() { return precoLitro; }

    public void setTamanhoDeposito(double tamDeposito) { this.tamDeposito = tamDeposito; }
    public void setConsumoLitros100km(double consumo) { this.consumo = consumo; }
    public void setPrecoLitro(double precoLitro) { this.precoLitro = precoLitro; } 
}