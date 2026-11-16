import java.time.LocalDate;
import java.util.Arrays;
import java.util.Scanner;

public class MainFicha2 {

    public static int[] lerArray() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Tamanho do array: ");
        int n = sc.nextInt();

        int[] a = new int[n];
        System.out.println("Insira " + n + " números: ");

        for (int i = 0; i < n; i++) {
            int num = sc.nextInt();
            a[i] = num;
        }
        return a;
    }

    public static void main(String[] args) {
        Ficha2 f2 = new Ficha2();
        Scanner sc = new Scanner(System.in);

        // Ex 1a
        /*
        int[] a = lerArray();
        System.out.println("a = " + Arrays.toString(a) + "\n");
        int min = f2.minArray(a);
        System.out.println("O mínimo é: " + min + "\n");
        */

        // Ex 1b
        /*
        int[] b = lerArray();
        System.out.println("b = " + Arrays.toString(b) + "\n");
        System.out.println("Insira o primeiro índice: ");
        int start = sc.nextInt();
        System.out.println("Insira o segundo índice: ");
        int end = sc.nextInt();
        int[] meio = f2.inBetween(b, start, end);
        System.out.println("meio = " + Arrays.toString(meio) + "\n");
        */

        // Ex 1c
        /*
        int[] c = lerArray();
        int[] d = lerArray();
        int[] common = f2.comuns(c,d);
        System.out.println("common = " + Arrays.toString(common) + "\n");
        */

        // Ex 2a, 2b, 1c
        /*
        f2.insereData((LocalDate.of(2024, 3, 10)));
        f2.insereData(LocalDate.of(2024, 5, 15));
        f2.insereData(LocalDate.of(2024, 7, 20));

        String res = f2.toString();
        System.out.println("Data mais próxima de 2025-03-27: " + f2.dataMaisProxima(LocalDate.of(2025, 3, 27)));
        */
    }
}