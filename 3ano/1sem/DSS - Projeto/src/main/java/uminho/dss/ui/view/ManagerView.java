package uminho.dss.ui.view;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ManagerView {
    private Scanner scanner;

    public ManagerView() {
        this.scanner = new Scanner(System.in);
    }

    public void displayAnalytics(int orderCount, BigDecimal revenue, double avgServiceTime) {
        printHeader("ESTATISTICAS DO RESTAURANTE (Ultimos 7 Dias)");
        System.out.println("  Total de Pedidos: " + orderCount);
        System.out.printf("  Receita Total: EUR %.2f%n", revenue);
        System.out.printf("  Tempo Medio de Servico: %.1f minutos%n", avgServiceTime);
        printDivider();
        System.out.println("\n  Prima Enter para voltar...");
        scanner.nextLine();
    }

    public void displayWorkstations(Set<String> workstations) {
        printHeader("ESTACOES ATIVAS");

        if (workstations.isEmpty()) {
            System.out.println("  Nenhuma estacao ativa.");
        } else {
            int index = 1;
            for (String ws : workstations) {
                System.out.println("  " + index + ". " + ws);
                index++;
            }
        }

        printDivider();
        System.out.println("\n  Prima Enter para voltar...");
        scanner.nextLine();
    }

    public void displayLowStock(Map<String, Double> lowStock) {
        if (lowStock.isEmpty()) {
            System.out.println("\n  Todos os ingredientes estao bem abastecidos.");
            return;
        }

        printHeader("ALERTA - INGREDIENTES COM STOCK BAIXO");
        System.out.printf("  %-40s %s%n", "Ingrediente", "Nivel");
        printDivider();

        for (var entry : lowStock.entrySet()) {
            String stockBar = createStockBar(entry.getValue());
            String name = entry.getKey().length() > 40 ? entry.getKey().substring(0, 37) + "..." : entry.getKey();
            System.out.printf("  %-40s %s %.0f%%%n", name, stockBar, entry.getValue());
        }

        printDivider();
        System.out.println("  Por favor considere reabastecer estes ingredientes");
        printDivider();
        System.out.println("\n  Prima Enter para voltar...");
        scanner.nextLine();
    }

    public String promptMessage() {
        printHeader("ENVIAR MENSAGEM");
        System.out.print("  Mensagem: ");
        return scanner.nextLine();
    }

    public void displayMessageSent(int restaurantId) {
        System.out.println("\n  Mensagem enviada para todos no restaurante " + restaurantId);
    }

    public void displayBestSellers(Map<String, Integer> bestSellers) {
        if (bestSellers.isEmpty()) {
            System.out.println("\n  Sem dados de vendas disponiveis.");
            return;
        }

        printHeader("TOP 10 ITENS MAIS VENDIDOS (Ultimos 30 Dias)");
        System.out.printf("  %-30s %s%n", "Item", "Qtd Vendida");
        printDivider();

        int rank = 1;
        for (var entry : bestSellers.entrySet()) {
            String name = entry.getKey().length() > 27 ? entry.getKey().substring(0, 24) + "..." : entry.getKey();
            System.out.printf("  %2d. %-27s %4d%n", rank++, name, entry.getValue());
        }

        printDivider();
        System.out.println("\n  Prima Enter para voltar...");
        scanner.nextLine();
    }

    public void showError(String message) {
        System.err.println("  ERRO: " + message);
    }

    private String createStockBar(double percentage) {
        int filled = (int) Math.round((percentage / 100.0) * 10);
        int empty = 10 - filled;
        filled = Math.max(0, Math.min(10, filled));
        empty = Math.max(0, Math.min(10, empty));
        return "[" + "#".repeat(filled) + "-".repeat(empty) + "]";
    }

    private void printHeader(String title) {
        System.out.println();
        printDivider();
        System.out.println("  " + title);
        printDivider();
    }

    private void printDivider() {
        System.out.println("  " + "=".repeat(50));
    }
}
