package uminho.dss.ui;

import java.util.Scanner;

public class UIHelper {

    private static final Scanner scanner = new Scanner(System.in);

    public static void printHeader(String title) {
        System.out.println();
        printDivider();
        System.out.println("  " + title);
        printDivider();
    }

    public static void printDivider() {
        System.out.println("  " + "=".repeat(50));
    }

    public static void showError(String message) {
        System.err.println("  ERRO: " + message);
    }

    public static void showSuccess(String message) {
        System.out.println("\n  ✓ " + message);
    }

    public static int promptRestaurantSelection() {
        printHeader("SELECIONAR RESTAURANTE");
        System.out.println("  1. Braga Centro");
        System.out.println("  2. Porto Baixa");
        System.out.println("  3. Lisboa Saldanha");
        printDivider();
        System.out.print("  Escolha restaurante: ");
        return Menu.readIntSafe(scanner, 1, 3);
    }

    public static LoginCredentials promptLogin(String title) {
        printHeader(title);
        System.out.print("  Username: ");
        String username = scanner.nextLine();
        System.out.print("  Password: ");
        String password = scanner.nextLine();
        return new LoginCredentials(username, password);
    }

    public static class LoginCredentials {
        public final String username;
        public final String password;

        public LoginCredentials(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
}
