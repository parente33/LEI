package uminho.dss.ui.view;
import uminho.dss.ui.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Scanner;

public class COOView {
    private Scanner scanner;

    public COOView() {
        this.scanner = new Scanner(System.in);
    }

    public void displayAllRestaurantsAnalytics(Map<Integer, RestaurantStats> statsMap) {
        printHeader("ESTATISTICAS GLOBAIS - TODOS OS RESTAURANTES");

        System.out.printf("  %-20s  %-10s  %-15s  %-15s%n",
                "Restaurante", "Pedidos", "Receita", "Tempo Medio");
        printDivider();

        for (var entry : statsMap.entrySet()) {
            RestaurantStats stats = entry.getValue();
            System.out.printf("  %-20s  %-10d  EUR %-11.2f  %.1f min%n",
                    "Restaurante " + entry.getKey(), stats.orderCount,
                    stats.revenue, stats.avgServiceTime);
        }

        printDivider();
        System.out.println("\n  Prima Enter para voltar...");
        scanner.nextLine();
    }

    public Integer promptRestaurantSelection() {
        printHeader("SELECIONAR RESTAURANTE");

        System.out.println("  1. Braga Centro");
        System.out.println("  2. Porto Baixa");
        System.out.println("  3. Lisboa Saldanha");
        System.out.println("  0. Voltar");

        Menu menu = new Menu();
        return menu.readInt("\nEscolha restaurante: ", "Invalido", 0, 3);
    }

    public void displayGlobalRevenue(BigDecimal totalRevenue, int totalOrders) {
        printHeader("RECEITA GLOBAL - CADEIA COMPLETA");
        System.out.printf("  Total de Pedidos: %d%n", totalOrders);
        System.out.printf("  Receita Total: EUR %.2f%n", totalRevenue);
        printDivider();
        System.out.println("\n  Prima Enter para voltar...");
        scanner.nextLine();
    }

    public void displayWorkstationDemand(Map<String, Integer> demand) {
        if (demand.isEmpty()) {
            System.out.println("\n  Sem dados de demanda disponiveis.");
            return;
        }

        printHeader("DEMANDA POR TIPO DE ESTACAO");
        System.out.printf("  %-40s  %s%n", "Estacao", "Tarefas");
        printDivider();

        for (var entry : demand.entrySet()) {
            String name = entry.getKey().length() > 40 ? entry.getKey().substring(0, 37) + "..." : entry.getKey();
            System.out.printf("  %-40s  %4d%n", name, entry.getValue());
        }

        printDivider();
        System.out.println("  Considere abrir mais postos nas estacoes com maior demanda");
        printDivider();
        System.out.println("\n  Prima Enter para voltar...");
        scanner.nextLine();
    }

    public void displayGlobalLowStock(Map<String, Double> lowStock) {
        if (lowStock.isEmpty()) {
            System.out.println("\n  Todos os ingredientes bem abastecidos em todos os restaurantes.");
            return;
        }

        printHeader("ALERTA GLOBAL - INGREDIENTES COM STOCK BAIXO");
        System.out.printf("  %-45s %s%n", "Ingrediente (Restaurante)", "Nivel");
        printDivider();

        for (var entry : lowStock.entrySet()) {
            String stockBar = createStockBar(entry.getValue());
            String name = entry.getKey().length() > 45 ? entry.getKey().substring(0, 42) + "..." : entry.getKey();
            System.out.printf("  %-45s %s %.0f%%%n", name, stockBar, entry.getValue());
        }

        printDivider();
        System.out.println("\n  Prima Enter para voltar...");
        scanner.nextLine();
    }

    public BroadcastData promptBroadcastMessage() {
        printHeader("ENVIAR MENSAGEM");

        System.out.println("  1. Broadcast Global (Todos os Restaurantes)");
        System.out.println("  2. Mensagem para Restaurante Especifico");
        System.out.println("  0. Cancelar");

        Menu menu = new Menu();
        int choice = menu.readInt("\nOpcao: ", "Invalido", 0, 2);

        if (choice == 0)
            return null;

        System.out.print("\nMensagem: ");
        String message = scanner.nextLine();

        if (choice == 1) {
            return new BroadcastData(message, null);
        } else {
            Integer restaurantId = promptRestaurantSelection();
            if (restaurantId == 0)
                return null;
            return new BroadcastData(message, restaurantId);
        }
    }

    public void displayMessageSent(boolean isGlobal, Integer restaurantId) {
        if (isGlobal) {
            System.out.println("\n  Mensagem enviada para TODOS os restaurantes!");
        } else {
            System.out.println("\n  Mensagem enviada para restaurante " + restaurantId);
        }
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

    public static class RestaurantStats {

        public final int orderCount;
        public final BigDecimal revenue;
        public final double avgServiceTime;

        public RestaurantStats(int orderCount, BigDecimal revenue, double avgServiceTime) {
            this.orderCount = orderCount;
            this.revenue = revenue;
            this.avgServiceTime = avgServiceTime;
        }
    }

    public static class BroadcastData {

        public final String message;
        public final Integer restaurantId;

        public BroadcastData(String message, Integer restaurantId) {
            this.message = message;
            this.restaurantId = restaurantId;
        }

        public boolean isGlobal() {
            return restaurantId == null;
        }
    }
}
