public class TesteCirculo {
    public static void main(String[] args) {
        
        Circulo c1 = new Circulo();
        System.out.println("Circulo c1 (por omissão): " + c1.toString());

        c1.setX(1.0);
        c1.setY(2.0);
        c1.setRaio(3.0);
        System.out.println("c1 atualizado: " + c1);

        Circulo c2 = new Circulo(4.0, 5.0, 6.0);
        System.out.println("Circulo c2 (parametrizado): " + c2);

        Circulo c3 = new Circulo(c2);
        System.out.println("Circulo c3 (cópia de c2): " + c3);

        System.out.println("c2 é igual a c3? " + c2.equals(c3));

        c3.alteraCentro(7.0, 8.0);
        System.out.println("c3 após mudar o centro: " + c3);

        System.out.println("Área de c3: " + c3.calculaArea());
        System.out.println("Perímetro de c3: " + c3.calculaPerimetro());

        Circulo c4 = c1.clone();
        System.out.println("Clone de c1 (c4): " + c4);
        System.out.println("c1 é igual a c4? " + c1.equals(c4));
    }
}