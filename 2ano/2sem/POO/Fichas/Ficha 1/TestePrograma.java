import java.util.Scanner;

public class TestePrograma {

    public static void main(String[] args) {
        Ficha1 f1 = new Ficha1();
        Scanner sc = new Scanner(System.in);

        // pergunta 1
        //System.out.println("Enter the value in *C: ");
        //double celsius = sc.nextDouble();
        //double farenheit = f1.celsiusParaFarenheit(celsius);
        //System.out.println(farenheit);

        // pergunta 2
        //System.out.printf("Enter your values: ");
        //int a = sc.nextInt();
        //int b = sc.nextInt();
        //int m = f1.maximoNumeros(a,b);
        //System.out.println(m);

        // pergunta 3
        //System.out.println("Enter your name: ");
        //String name = sc.nextLine();
        //System.out.println("Enter your balance: ");
        //double saldo = sc.nextDouble();
        //String descricao = f1.criaDescricaoConta(name,saldo);
        //System.out.println(descricao);

        // pergunta 4
        //System.out.println("Enter the value in euros: ");
        //double valor = sc.nextDouble();
        //System.out.println("Enter the convertion rate: ");
        //double taxa = sc.nextDouble();
        //double res = f1.eurosParaLibras(valor, taxa);
        //System.out.println(res);

        // pergunta 5
        //System.out.println("Enter your values: ");
        //int a = sc.nextInt();
        //int b = sc.nextInt();
        //int maior = f1.maximoNumeros(a,b);
        //if (maior == a) {
        //    System.out.printf("%d %d\n", maior, b);
        //} else {
        //    System.out.printf("%d %d\n", maior, a);
        //}

        // pergunta 6
        System.out.println("Enter your value: ");
        int a = sc.nextInt();
        long res = f1.factorial(a);
        System.out.println(res);
    }
}