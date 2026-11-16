package uminho.dss.ui.view;
import uminho.dss.ui.*;

import uminho.dss.business.order.Order;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DeliveryView {

    private Scanner scanner;
    private String deliveryType;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public DeliveryView(String deliveryType) {
        this.scanner = new Scanner(System.in);
        this.deliveryType = deliveryType;
    }

    public void displayReadyOrders(List<Order> orders) {
        String title = deliveryType.equals("TAKEAWAY") ? "PEDIDOS PRONTOS - TAKEAWAY" : "PEDIDOS PRONTOS - BALCAO";

        printHeader(title);

        if (orders.isEmpty()) {
            System.out.println("  Nenhum pedido pronto para entrega.");
            printDivider();
            System.out.println("\n  Prima Enter para voltar...");
            scanner.nextLine();
            return;
        }

        System.out.printf("  %-12s  %-10s  %-10s  %-8s  %s%n",
                "Pedido", "Tipo", "Balcao", "Pronto", "Itens");
        printDivider();

        for (Order order : orders) {
            String timeDisplay = order.getEstimatedCompletionTime() != null
                    ? order.getEstimatedCompletionTime().format(TIME_FORMAT)
                    : "--:--";

            String counterDisplay = "N/A";
            if (order.getOrderType().equals("TAKEAWAY")) {
                counterDisplay = "Takeaway";
            }

            System.out.printf("  %-12s  %-10s  %-10s  %-8s  %d%n",
                    order.getOrderNumber(), order.getOrderType(), counterDisplay,
                    timeDisplay, order.getOrderItemIds().size());
        }

        printDivider();
        System.out.println("\n  Prima Enter para voltar...");
        scanner.nextLine();
    }

    public String promptOrderSelection(List<Order> readyOrders) {
        if (readyOrders.isEmpty()) {
            System.out.println("\n  Nenhum pedido pronto.");
            return null;
        }

        printHeader("ENTREGAR PEDIDO");

        List<String> orderNumbers = new ArrayList<>();
        for (int i = 0; i < readyOrders.size(); i++) {
            Order order = readyOrders.get(i);
            System.out.printf("  %d. %-12s  %d itens%n",
                    (i + 1), order.getOrderNumber(), order.getOrderItemIds().size());
            orderNumbers.add(order.getOrderNumber());
        }
        System.out.println("  0. Cancelar");

        Menu menu = new Menu();
        int choice = menu.readInt("\nEscolha pedido: ", "Invalido", 0, orderNumbers.size());

        return choice == 0 ? null : orderNumbers.get(choice - 1);
    }

    public DeliveryAction promptDeliveryAction() {
        System.out.println("\n  1. Confirmar Entrega");
        System.out.println("  2. Cliente Nao Apareceu");
        System.out.println("  0. Voltar");

        Menu menu = new Menu();
        int choice = menu.readInt("\nOpcao: ", "Invalido", 0, 2);

        return switch (choice) {
            case 1 -> DeliveryAction.COMPLETE;
            case 2 -> DeliveryAction.NO_SHOW;
            default -> DeliveryAction.CANCEL;
        };
    }

    public void displayDeliveryCompleted(String orderNumber) {
        System.out.println("\n  Pedido " + orderNumber + " entregue com sucesso!");
    }

    public void displayNoShow(String orderNumber) {
        System.out.println("\n  Pedido " + orderNumber + " marcado como 'Cliente nao apareceu'.");
    }

    public void displayMessages(java.util.Map<String, String> messages) {
        printHeader("MENSAGENS RECENTES");

        if (messages.isEmpty()) {
            System.out.println("  Sem mensagens.");
            printDivider();
            return;
        }

        int index = 1;
        for (var entry : messages.entrySet()) {
            System.out.printf("  %2d. %s%n", index++, entry.getValue());
            System.out.println("      " + entry.getKey());
        }
        printDivider();
    }

    public void showError(String message) {
        System.err.println("  ERRO: " + message);
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

    public enum DeliveryAction {
        COMPLETE, NO_SHOW, CANCEL
    }
}
