package uminho.dss.ui.view;
import uminho.dss.ui.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class KioskView {
    private Scanner scanner;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public KioskView() {
        this.scanner = new Scanner(System.in);
    }

    public OrderTypeData promptOrderType() {
        printHeader("NOVO PEDIDO");
        System.out.println("  1. Comer no Local");
        System.out.println("  2. Takeaway");

        Menu menu = new Menu();
        int typeChoice = menu.readInt("\n  Escolha: ", "Opcao invalida", 1, 2);
        String orderType = typeChoice == 1 ? "DINE_IN" : "TAKEAWAY";

        printDivider();
        System.out.println("  METODO DE PAGAMENTO");
        printDivider();
        System.out.println("  1. MB Way");
        System.out.println("  2. Dinheiro");
        System.out.println("  3. Cartao");

        int paymentChoice = menu.readInt("\n  Escolha: ", "Opcao invalida", 1, 3);
        String paymentMethod = switch (paymentChoice) {
            case 1 -> "MBWAY";
            case 2 -> "CASH";
            case 3 -> "CARD";
            default -> "CARD";
        };

        return new OrderTypeData(orderType, paymentMethod);
    }

    public void displayOrderCreated(String orderNumber) {
        System.out.println("\n  Pedido criado: " + orderNumber);
        System.out.println("  Tempo estimado: ~15 minutos\n");
    }

    public int showOrderMenu(String orderNumber) {
        printDivider();
        System.out.println("  PEDIDO " + orderNumber);
        printDivider();
        System.out.println("  1. Adicionar Item");
        System.out.println("  2. Personalizar Item");
        System.out.println("  3. Adicionar Notas");
        System.out.println("  4. Ver Total");
        System.out.println("  5. Finalizar e Pagar");
        System.out.println("  0. Cancelar");

        Menu menu = new Menu();
        return menu.readInt("\n  Opcao: ", "Opcao invalida", 0, 5);
    }

    public String selectCategory(Set<String> categories, Set<String> excludedAllergens) {
        List<String> orderedCodes = Arrays.asList("MENU", "MAIN", "DRINK", "SIDE");
        Map<String, String> displayNames = Map.of(
                "MENU", "Menus",
                "MAIN", "Sanduiches e Refeicoes",
                "DRINK", "Bebidas",
                "SIDE", "Acompanhamentos");

        List<String> menuOptions = new ArrayList<>();
        for (String code : orderedCodes) {
            if (categories.contains(code))
                menuOptions.add(code);
        }
        for (String cat : categories) {
            if (!menuOptions.contains(cat))
                menuOptions.add(cat);
        }

        printHeader("SELECIONAR CATEGORIA");

        if (!excludedAllergens.isEmpty()) {
            System.out.println("  FILTRO ATIVO - Excluindo: " + String.join(", ", excludedAllergens));
            printDivider();
        }

        for (int i = 0; i < menuOptions.size(); i++) {
            String code = menuOptions.get(i);
            String displayName = displayNames.getOrDefault(code, code);
            System.out.printf("  %d. %s%n", (i + 1), displayName);
        }
        System.out.println("  0. Voltar");

        Menu menu = new Menu();
        int choice = menu.readInt("\n  Opcao: ", "Opcao invalida", 0, menuOptions.size());

        return choice == 0 ? null : menuOptions.get(choice - 1);
    }

    public ItemSelection selectItem(List<String> items, Map<String, BigDecimal> prices,
            Map<String, Set<String>> allergens, String categoryName) {
        printHeader(categoryName.toUpperCase());

        for (int i = 0; i < items.size(); i++) {
            String itemName = items.get(i);
            BigDecimal price = prices.get(itemName);
            Set<String> itemAllergens = allergens.get(itemName);

            System.out.printf("  %d. %-30s  EUR %.2f%n", (i + 1), itemName, price);
            if (itemAllergens != null && !itemAllergens.isEmpty()) {
                System.out.printf("     Alergenios: %s%n", String.join(", ", itemAllergens));
            }
        }
        System.out.println("  0. Voltar");

        Menu menu = new Menu();
        int itemIndex = menu.readInt("\n  Escolha item: ", "Opcao invalida", 0, items.size());

        if (itemIndex == 0)
            return null;

        String itemName = items.get(itemIndex - 1);
        int quantity = menu.readInt("  Quantidade: ", "Quantidade invalida", 1, 100);

        return new ItemSelection(itemName, quantity);
    }

    public void displayItemAdded(String itemName, int quantity) {
        System.out.println("\n  " + quantity + "x " + itemName + " adicionado!");
    }

    public void displayNoItemsInCategory() {
        System.out.println("\n  Sem itens disponiveis nesta categoria.");
    }

    public String selectItemToCustomize(List<String> items) {
        if (items.isEmpty()) {
            System.out.println("\n  O carrinho esta vazio.");
            return null;
        }

        printHeader("PERSONALIZAR ITEM");

        for (int i = 0; i < items.size(); i++) {
            System.out.printf("  %d. %s%n", (i + 1), items.get(i));
        }
        System.out.println("  0. Cancelar");

        Menu menu = new Menu();
        int index = menu.readInt("\n  Escolha item: ", "Opcao invalida", 0, items.size());

        if (index == 0)
            return null;

        String selectedItem = items.get(index - 1);
        System.out.println("\n  A personalizar: " + selectedItem);
        return selectedItem;
    }

    public void displayAlternatives(Map<String, Set<String>> alternatives) {
        if (!alternatives.isEmpty()) {
            System.out.println("\n  Substituicoes disponiveis:");
            for (var entry : alternatives.entrySet()) {
                System.out.println("    " + entry.getKey() + " -> " + entry.getValue());
            }
        }
    }

    public CustomizationData promptCustomization() {
        System.out.print("\n  Ingrediente a remover (Enter para ignorar): ");
        String remove = scanner.nextLine().trim();
        if (remove.isEmpty())
            remove = null;

        System.out.print("  Ingrediente a adicionar (Enter para ignorar): ");
        String add = scanner.nextLine().trim();
        if (add.isEmpty())
            add = null;

        System.out.print("  Instrucao de cozinha (ex: 'bem passado'): ");
        String instruction = scanner.nextLine().trim();
        if (instruction.isEmpty())
            instruction = null;

        return new CustomizationData(remove, add, instruction);
    }

    public void displayCustomizationSuccess() {
        System.out.println("\n  Item personalizado!");
    }

    public void displayNoChanges() {
        System.out.println("\n  Sem alteracoes.");
    }

    public String promptOrderNote() {
        printHeader("ADICIONAR NOTAS");
        System.out.println("  Digite instrucoes especiais (Enter para cancelar):");
        System.out.print("  > ");

        String note = scanner.nextLine().trim();
        return note.isEmpty() ? null : note;
    }

    public void displayNoteAdded() {
        System.out.println("\n  Nota adicionada!");
    }

    public void displayTotal(BigDecimal total) {
        System.out.printf("%n  Total atual: EUR %.2f%n", total);
    }

    public boolean confirmPayment(BigDecimal total) {
        printHeader("CONFIRMACAO DE PAGAMENTO");
        System.out.printf("  Total: EUR %.2f%n", total);

        System.out.print("\n  Confirmar pagamento? (s/n): ");
        String confirm = scanner.nextLine().trim();
        return confirm.equalsIgnoreCase("s") || confirm.equalsIgnoreCase("y");
    }

    public void displayPaymentCancelled() {
        System.out.println("\n  Pagamento cancelado. Continue a adicionar itens.");
    }

    public void displayReceipt(String orderNumber, LocalDateTime estimatedTime,
            String orderType, Integer counterNumber) {
        printHeader("PAGAMENTO CONCLUIDO");
        System.out.println("  Numero do Pedido: " + orderNumber);
        System.out.println("  Pronto as: " + estimatedTime.format(TIME_FORMAT));

        if ("TAKEAWAY".equals(orderType)) {
            System.out.println("\n  Levante o seu pedido no Balcao Principal.");
        } else if (counterNumber != null) {
            System.out.println("\n  Dirija-se ao Balcao " + counterNumber + " para levantar.");
        }

        System.out.println("\n  Aguarde pelo seu numero!");
        printDivider();
    }

    public String manageAllergenFilter(Set<String> currentlyExcluded) {
        printHeader("FILTRO DE ALERGENIOS");

        if (currentlyExcluded.isEmpty()) {
            System.out.println("  Nenhum alergenio excluido.");
        } else {
            System.out.println("  Alergenios excluidos: " + String.join(", ", currentlyExcluded));
        }

        printDivider();
        System.out.println("  ALERGENIOS COMUNS:");
        System.out.println("  1. GLUTEN      (pao, massas)");
        System.out.println("  2. DAIRY       (lacticinios)");
        System.out.println("  3. EGGS        (ovos)");
        System.out.println("  4. NUTS        (frutos secos)");
        System.out.println("  5. SHELLFISH   (mariscos)");
        System.out.println("  6. SOY         (soja)");
        printDivider();
        System.out.println("  7. Limpar todos os filtros");
        System.out.println("  0. Voltar");

        Menu menu = new Menu();
        int choice = menu.readInt("\n  Opcao: ", "Opcao invalida", 0, 7);

        return switch (choice) {
            case 0 -> "EXIT";
            case 1 -> "GLUTEN";
            case 2 -> "DAIRY";
            case 3 -> "EGGS";
            case 4 -> "NUTS";
            case 5 -> "SHELLFISH";
            case 6 -> "SOY";
            case 7 -> "CLEAR_ALL";
            default -> null;
        };
    }

    public void displayAllergenToggled(String allergen, boolean added) {
        if (added) {
            System.out.println("\n  " + allergen + " adicionado ao filtro.");
        } else {
            System.out.println("\n  " + allergen + " removido do filtro.");
        }
    }

    public void displayAllergensCleared() {
        System.out.println("\n  Filtros limpos.");
    }

    public void displayMenu(Map<String, List<String>> byCategory, Map<String, BigDecimal> prices,
            Map<String, Set<String>> allergens, Set<String> excludedAllergens) {
        printHeader("MENU");

        if (!excludedAllergens.isEmpty()) {
            System.out.println("  FILTRO ATIVO - Excluindo: " + String.join(", ", excludedAllergens));
            printDivider();
        }

        for (var entry : byCategory.entrySet()) {
            System.out.println("\n  >>> " + entry.getKey() + " <<<");
            printDivider();
            for (String item : entry.getValue()) {
                BigDecimal price = prices.get(item);
                System.out.printf("    %-32s  EUR %6.2f%n", item, price);

                Set<String> itemAllergens = allergens.get(item);
                if (itemAllergens != null && !itemAllergens.isEmpty()) {
                    System.out.printf("      Alergenios: %s%n", String.join(", ", itemAllergens));
                }
            }
        }
        printDivider();

        System.out.println("\n  Prima Enter para voltar...");
        scanner.nextLine();
    }

    public void displayOrderStatus(List<OrderStatusInfo> orders) {
        printHeader("ESTADO DOS PEDIDOS");

        if (orders.isEmpty()) {
            System.out.println("  Sem pedidos ativos no momento.");
            printDivider();
            System.out.println("\n  Prima Enter para voltar...");
            scanner.nextLine();
            return;
        }

        System.out.printf("  %-10s  %-15s  %-10s  %-10s  %s%n", "Pedido", "Estado", "Balcao", "Pronto", "Itens");
        printDivider();

        for (OrderStatusInfo order : orders) {
            System.out.printf("  %-10s  %-15s  %-10s  %-10s  %d itens%n",
                    order.orderNumber, order.statusDisplay, order.counterDisplay,
                    order.timeDisplay, order.itemCount);
        }

        printDivider();
        System.out.println("\n  Prima Enter para voltar...");
        scanner.nextLine();
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

    public static class OrderTypeData {
        public final String orderType;
        public final String paymentMethod;

        public OrderTypeData(String orderType, String paymentMethod) {
            this.orderType = orderType;
            this.paymentMethod = paymentMethod;
        }
    }

    public static class ItemSelection {
        public final String itemName;
        public final int quantity;

        public ItemSelection(String itemName, int quantity) {
            this.itemName = itemName;
            this.quantity = quantity;
        }
    }

    public static class CustomizationData {
        public final String removeIngredient;
        public final String addIngredient;
        public final String cookingInstruction;

        public CustomizationData(String removeIngredient, String addIngredient, String cookingInstruction) {
            this.removeIngredient = removeIngredient;
            this.addIngredient = addIngredient;
            this.cookingInstruction = cookingInstruction;
        }

        public boolean hasChanges() {
            return removeIngredient != null || addIngredient != null || cookingInstruction != null;
        }
    }

    public static class OrderStatusInfo {
        public final String orderNumber;
        public final String statusDisplay;
        public final String counterDisplay;
        public final String timeDisplay;
        public final int itemCount;

        public OrderStatusInfo(String orderNumber, String statusDisplay, String counterDisplay,
                String timeDisplay, int itemCount) {
            this.orderNumber = orderNumber;
            this.statusDisplay = statusDisplay;
            this.counterDisplay = counterDisplay;
            this.timeDisplay = timeDisplay;
            this.itemCount = itemCount;
        }
    }
}
