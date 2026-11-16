package uminho.dss.ui.view;
import uminho.dss.ui.*;

import uminho.dss.business.kitchen.TaskInfo;

import java.util.List;
import java.util.Scanner;

public class EmployeeView {
    private Scanner scanner;

    public EmployeeView() {
        this.scanner = new Scanner(System.in);
    }

    public void displayTasks(List<TaskInfo> tasks, String workstationCode) {
        if (tasks.isEmpty()) {
            printHeader("TAREFAS");
            System.out.println("  Sem tarefas pendentes.");
            printDivider();
            return;
        }

        printHeader("TAREFAS - " + workstationCode);

        long pending = tasks.stream().filter(t -> t.getStatus().equals("PENDING")).count();
        long inProgress = tasks.stream().filter(t -> t.getStatus().equals("IN_PROGRESS")).count();

        System.out.printf("  Pendentes: %d | Em Progresso: %d%n", pending, inProgress);
        printDivider();

        System.out.printf("  %-6s  %-10s  %-25s  %-10s%n", "ID", "Pedido", "Descricao", "Estado");
        printDivider();

        for (TaskInfo task : tasks) {
            String statusDisplay = formatTaskStatus(task.getStatus());
            String fullDesc = task.getDescription();
            String itemPart = fullDesc.contains(" | ") ? fullDesc.split(" \\| ")[0] : fullDesc;

            if (itemPart.length() > 25)
                itemPart = itemPart.substring(0, 22) + "...";

            System.out.printf("  %-6d  %-10s  %-25s  %-10s%n",
                    task.getTaskId(), task.getOrderNumber(), itemPart, statusDisplay);
        }

        printDivider();
        System.out.println("  Total: " + tasks.size() + " tarefa(s)");
    }

    public Integer promptTaskSelection(List<TaskInfo> tasks, String action) {
        if (tasks.isEmpty()) {
            printHeader("SELECIONAR TAREFA");
            System.out.println("  Sem tarefas disponiveis.");
            printDivider();
            return null;
        }

        printHeader(action.toUpperCase());
        System.out.printf("  %-4s  %-6s  %-10s  %-30s%n", "#", "ID", "Pedido", "Descricao");
        printDivider();

        for (int i = 0; i < tasks.size(); i++) {
            TaskInfo task = tasks.get(i);
            String fullDesc = task.getDescription();
            String itemPart = fullDesc.contains(" | ") ? fullDesc.split(" \\| ")[0] : fullDesc;
            if (itemPart.length() > 30)
                itemPart = itemPart.substring(0, 27) + "...";

            String marker = task.getStatus().equals("DELAYED") ? "! " : "  ";
            System.out.printf("  %-4d  %-6d  %-10s  %s%s%n",
                    (i + 1), task.getTaskId(), task.getOrderNumber(), marker, itemPart);
        }

        System.out.println("  0. Cancelar");
        printDivider();

        Menu menu = new Menu();
        int choice = menu.readInt("  Escolha: ", "Opcao invalida", 0, tasks.size());

        return choice == 0 ? null : tasks.get(choice - 1).getTaskId();
    }

    public boolean confirmTaskCompletion(TaskInfo task) {
        printHeader("CONCLUIR TAREFA");

        String fullDesc = task.getDescription();
        String itemPart = fullDesc.contains(" | ") ? fullDesc.split(" \\| ")[0] : fullDesc;

        System.out.println("  Tarefa em curso:");
        System.out.println("    ID: " + task.getTaskId());
        System.out.println("    Pedido: " + task.getOrderNumber());
        System.out.println("    Item: " + itemPart);
        printDivider();

        System.out.print("  Confirmar conclusao? (s/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        return confirm.equals("s") || confirm.equals("sim");
    }

    public DelayData promptTaskDelay(TaskInfo task) {
        printHeader("ATRASAR TAREFA");

        System.out.println("  Tarefa atual: " + task.getTaskId() +
                " (" + task.getOrderNumber() + ")");
        printDivider();

        Menu menu = new Menu();
        int delayMinutes = menu.readInt("  Minutos de atraso: ", "Minutos invalidos", 1, 120);

        System.out.print("  Motivo do atraso: ");
        String reason = scanner.nextLine();

        return new DelayData(delayMinutes, reason);
    }

    public IngredientRequest promptIngredientRequest() {
        printHeader("PEDIR INGREDIENTE");

        System.out.print("  Nome do ingrediente: ");
        String ingredientName = scanner.nextLine().trim();

        Menu menu = new Menu();
        double quantity = menu.readInt("  Quantidade: ", "Quantidade invalida", 1, 10000);

        return new IngredientRequest(ingredientName, quantity);
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

    public void displayTaskStarted(int taskId) {
        System.out.println("\n  Tarefa " + taskId + " iniciada!");
    }

    public void displayTaskCompleted(int taskId) {
        System.out.println("\n  Tarefa " + taskId + " concluida!");
    }

    public void displayTaskDelayed(int taskId, int delayMinutes) {
        System.out.println("\n  Tarefa " + taskId + " atrasada " + delayMinutes + " minutos.");
    }

    public void displayIngredientRequested(String ingredientName, double quantity) {
        System.out.println("\n  Pedido enviado!");
        System.out.println("  " + ingredientName + " (" + quantity + " unidades)");
    }

    public void displayNoTaskInProgress() {
        printHeader("AVISO");
        System.out.println("  Nenhuma tarefa em curso.");
        System.out.println("  Inicie uma tarefa primeiro.");
        printDivider();
    }

    public void displayAlreadyInProgress() {
        printHeader("AVISO");
        System.out.println("  Ja tem uma tarefa em curso.");
        System.out.println("  Conclua-a antes de iniciar outra.");
        printDivider();
    }

    public void displayOperationCancelled() {
        System.out.println("\n  Operacao cancelada.");
    }

    public void showError(String message) {
        System.err.println("  ERRO: " + message);
    }

    private String formatTaskStatus(String status) {
        return switch (status) {
            case "PENDING" -> "Pendente";
            case "IN_PROGRESS" -> "EM CURSO";
            case "WAITING" -> "A espera";
            case "COMPLETED" -> "Concluida";
            case "DELAYED" -> "ATRASADA";
            default -> status;
        };
    }

    private void printHeader(String title) {
        System.out.println();
        printDivider();
        System.out.println("  " + title);
        printDivider();
    }

    private void printDivider() {
        System.out.println("  " + "-".repeat(50));
    }

    public static class DelayData {

        public final int delayMinutes;
        public final String reason;

        public DelayData(int delayMinutes, String reason) {
            this.delayMinutes = delayMinutes;
            this.reason = reason;
        }
    }

    public static class IngredientRequest {

        public final String ingredientName;
        public final double quantity;

        public IngredientRequest(String ingredientName, double quantity) {
            this.ingredientName = ingredientName;
            this.quantity = quantity;
        }
    }
}
