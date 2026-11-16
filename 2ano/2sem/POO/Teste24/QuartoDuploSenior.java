public class QuartoDuploSenior extends Quarto {
    private double precoBasePorDia;

    public QuartoDuploSenior(String numQuarto, double precoBasePorDia) {
        super(numQuarto);
        this.precoBasePorDia = precoBasePorDia;
    }

    @Override
    public double precoPorDia() {
        return precoBasePorDia;
    }
}