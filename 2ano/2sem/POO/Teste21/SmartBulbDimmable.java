public class SmartBulbDimmable extends SmartBulb {
    private double intensidade;
    private boolean primeiroUso;

    public SmartBulbDimmable(String id, int tone, double consumoPorHora, int intensidade) {
        super(id,tone,consumoPorHora);
        this.intensidade = intensidade;
        this.primeiroUso = true;
    }

    public double getIntensidade() {
        return this.intensidade;
    }

    public void setIntensidade(double novaIntensidade) {
        if (novaIntensidade < 0) this.intensidade = 0;
        else if (novaIntensidade > 100) this.intensidade = 100;
        else this.intensidade = novaIntensidade;
    }

    public boolean isFirstUse() {
        return this.primeiroUso;
    }

    @Override
    public void turnOn() {
        super.turnOn();
        if (primeiroUso) {
            this.intensidade = 50.0;
            primeiroUso = false;
        }
    }

    @Override
    public double totalConsumo() {
        double consumo = super.totalConsumo();
        if (primeiroUso) {
            return consumo * 0.5;
        }
        return consumo;
    }
}