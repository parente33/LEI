public class TestePonto {
    public static void main(String[] args) {
        Ponto ponto1 = new Ponto(3, 4);

        System.out.println("Coordenadas do Ponto 1:");
        System.out.println("X: " + ponto1.getX());
        System.out.println("Y: " + ponto1.getY());

        ponto1.setX(5);
        ponto1.setY(6);

        System.out.println("\nCoordenadas do Ponto 1 após alteração:");
        System.out.println("X: " + ponto1.getX());
        System.out.println("Y: " + ponto1.getY());

        Ponto ponto2 = new Ponto(1, 2);

        System.out.println("\nCoordenadas do Ponto 2:");
        System.out.println("X: " + ponto2.getX());
        System.out.println("Y: " + ponto2.getY());
    }
}