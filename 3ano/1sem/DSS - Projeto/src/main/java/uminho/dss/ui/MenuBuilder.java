package uminho.dss.ui;

import uminho.dss.ui.controller.*;

import java.util.Arrays;

public class MenuBuilder {

    public static Menu buildKioskMenu(KioskController controller) {
        Menu menu = new Menu("FastFood - Quiosque de Pedidos", Arrays.asList(
                "Novo Pedido",
                "Filtrar por Alergenios",
                "Ver Menu",
                "Estado do Pedido"));

        menu.setHandler(1, () -> safe(() -> controller.handleNewOrder()));
        menu.setHandler(2, () -> controller.handleFilterAllergens());
        menu.setHandler(3, () -> safe(() -> controller.handleViewMenu()));
        menu.setHandler(4, () -> safe(() -> controller.handleOrderStatus()));

        return menu;
    }

    public static Menu buildEmployeeMenu(EmployeeController controller) {
        Menu menu = new Menu("Estacao - " + controller.getEmployeeName(), Arrays.asList(
                "Ver Tarefas",
                "Iniciar Tarefa",
                "Concluir Tarefa",
                "Atrasar Tarefa",
                "Pedir Ingrediente",
                "Ver Mensagens"));

        menu.setHandler(1, () -> safe(() -> controller.handleViewTasks()));
        menu.setHandler(2, () -> safe(() -> controller.handleStartTask()));
        menu.setHandler(3, () -> safe(() -> controller.handleCompleteTask()));
        menu.setHandler(4, () -> safe(() -> controller.handleDelayTask()));
        menu.setHandler(5, () -> safe(() -> controller.handleRequestIngredient()));
        menu.setHandler(6, () -> safe(() -> controller.handleViewMessages()));

        return menu;
    }

    public static Menu buildDeliveryMenu(DeliveryController controller) {
        String title = controller.getDeliveryType().equals("TAKEAWAY") ? "Entregas - Takeaway" : "Entregas - Balcao";

        Menu menu = new Menu(title, Arrays.asList(
                "Ver Pedidos Prontos",
                "Entregar Pedido",
                "Ver Mensagens"));

        menu.setHandler(1, () -> safe(() -> controller.handleViewReadyOrders()));
        menu.setHandler(2, () -> safe(() -> controller.handleDeliverOrder()));
        menu.setHandler(3, () -> safe(() -> controller.handleViewMessages()));

        return menu;
    }

    public static Menu buildManagerMenu(ManagerController controller) {
        Menu menu = new Menu("Menu Principal - Gestor", Arrays.asList(
                "Ver Estatisticas do Restaurante",
                "Ver Estacoes Ativas",
                "Verificar Stock Baixo",
                "Enviar Mensagem",
                "Ver Itens Mais Vendidos"));

        menu.setHandler(1, () -> safe(() -> controller.handleViewAnalytics()));
        menu.setHandler(2, () -> safe(() -> controller.handleViewWorkstations()));
        menu.setHandler(3, () -> safe(() -> controller.handleCheckLowStock()));
        menu.setHandler(4, () -> safe(() -> controller.handleSendMessage()));
        menu.setHandler(5, () -> safe(() -> controller.handleViewBestSellers()));

        return menu;
    }

    public static Menu buildCOOMenu(COOController controller) {
        Menu menu = new Menu("Dashboard COO", Arrays.asList(
                "Ver Estatisticas de Todos os Restaurantes",
                "Ver Estatisticas de Um Restaurante",
                "Ver Receita Global",
                "Ver Demanda de Estacoes",
                "Ver Stock Baixo Global",
                "Enviar Mensagem"));

        menu.setHandler(1, () -> safe(() -> controller.handleViewAllRestaurants()));
        menu.setHandler(2, () -> safe(() -> controller.handleViewSingleRestaurant()));
        menu.setHandler(3, () -> safe(() -> controller.handleViewGlobalRevenue()));
        menu.setHandler(4, () -> safe(() -> controller.handleViewWorkstationDemand()));
        menu.setHandler(5, () -> safe(() -> controller.handleViewGlobalLowStock()));
        menu.setHandler(6, () -> safe(() -> controller.handleSendMessage()));

        return menu;
    }

    private static void safe(UIExceptionHandler handler) {
        try {
            handler.run();
        } catch (UIException e) {
            UIHelper.showError(e.getMessage());
        }
    }

    @FunctionalInterface
    private interface UIExceptionHandler {
        void run() throws UIException;
    }
}
