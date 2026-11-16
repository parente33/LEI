public class TesteSensor {
    public static void main(String[] args) {
        Sensor s1 = new Sensor();
        System.out.println("Sensor s1 (omissão): " + s1);

        Sensor s2 = new Sensor(10.5);
        System.out.println("Sensor s2 (parametrizado): " + s2);

        Sensor s3 = new Sensor(-5.0);
        System.out.println("Sensor s3 (negativo): " + s3);

        boolean atualizado = s1.setPressao(20.0);
        System.out.println("Atualização válida em s1: " + atualizado + " -> " + s1);

        boolean falhou = s1.setPressao(-3.0);
        System.out.println("Atualização inválida em s1: " + falhou + " -> " + s1);

        Sensor s4 = new Sensor(s2);
        System.out.println("Sensor s4 (cópia de s2): " + s4);

        Sensor s5 = s1.clone();
        System.out.println("Sensor s5 (clone de s1): " + s5);

        System.out.println("s2 é igual a s4? " + s2.equals(s4));
        System.out.println("s1 é igual a s5? " + s1.equals(s5));
    }
}