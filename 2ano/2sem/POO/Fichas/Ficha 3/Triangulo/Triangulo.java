public class Triangulo {
    private Ponto p1, p2, p3;

    // Construtores
    public Triangulo() {
        this.p1 = new Ponto(0, 0);
        this.p2 = new Ponto(0, 0);
        this.p3 = new Ponto(0, 0);
    }

    public Triangulo(Ponto p1, Ponto p2, Ponto p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    // Métodos de acesso e alteração
    public Ponto getP1() {
        return p1;
    }

    public void setP1(Ponto p1) {
        this.p1 = p1;
    }

    public Ponto getP2() {
        return p2;
    }

    public void setP2(Ponto p2) {
        this.p2 = p2;
    }

    public Ponto getP3() {
        return p3;
    }

    public void setP3(Ponto p3) {
        this.p3 = p3;
    }

    // Método que calcula a área do triângulo usando a fórmula de Herão
    public double calculaAreaTriangulo() {
        double a = calculaDistancia(p1, p2);
        double b = calculaDistancia(p2, p3);
        double c = calculaDistancia(p3, p1);

        double semiPerimetro = (a + b + c) / 2;
        return Math.sqrt(semiPerimetro * (semiPerimetro - a) * (semiPerimetro - b) * (semiPerimetro - c));
    }

    // Método que calcula o perímetro do triângulo
    public double calculaPerimetroTriangulo() {
        double a = calculaDistancia(p1, p2);
        double b = calculaDistancia(p2, p3);
        double c = calculaDistancia(p3, p1);
        return a + b + c;
    }

    // Método que calcula a altura do triângulo (distância entre o ponto com menor coordenada y e o de maior coordenada y)
    public double calculaAlturaTriangulo() {
        double yMin = Math.min(Math.min(p1.getY(), p2.getY()), p3.getY());
        double yMax = Math.max(Math.max(p1.getY(), p2.getY()), p3.getY());
        return yMax - yMin;
    }

    // Método auxiliar que calcula a distância entre dois pontos
    private double calculaDistancia(Ponto p1, Ponto p2) {
        return Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));
    }
}