public class TesteTriangulo {
    public static void main(String[] args) {
        Ponto p1 = new Ponto(0, 0);
        Ponto p2 = new Ponto(4, 0);
        Ponto p3 = new Ponto(0, 3);

        Triangulo triangulo = new Triangulo(p1, p2, p3);

        System.out.println("Área do Triângulo: " + triangulo.calculaAreaTriangulo());

        System.out.println("Perímetro do Triângulo: " + triangulo.calculaPerimetroTriangulo());

        System.out.println("Altura do Triângulo: " + triangulo.calculaAlturaTriangulo());
    }
}