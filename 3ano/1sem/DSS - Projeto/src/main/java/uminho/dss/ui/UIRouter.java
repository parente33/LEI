package uminho.dss.ui;

import uminho.dss.business.BusinessException;
import uminho.dss.ui.controller.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UIRouter {

    private ViewFactory factory;
    private SessionContext context;

    public UIRouter(ViewFactory factory, SessionContext context) {
        this.factory = factory;
        this.context = context;
    }

    public void showMainMenu() {
        Menu mainMenu = new Menu("Menu Principal", Arrays.asList(
                "Quiosque Cliente",
                "Estacao de Trabalho",
                "Gestor de Restaurante",
                "Dashboard COO"));

        mainMenu.setHandler(1, this::launchKiosk);
        mainMenu.setHandler(2, this::launchEmployeeInterface);
        mainMenu.setHandler(3, this::launchManagerInterface);
        mainMenu.setHandler(4, this::launchCOOInterface);

        mainMenu.run();
    }

    private void launchKiosk() {
        int restaurantId = UIHelper.promptRestaurantSelection();
        int kioskId = (restaurantId - 1) * 2 + 1;

        KioskController controller = factory.createKioskController(kioskId, restaurantId);
        Menu kioskMenu = MenuBuilder.buildKioskMenu(controller);
        kioskMenu.run();
    }

    private void launchEmployeeInterface() {
        try {
            UIHelper.LoginCredentials creds = UIHelper.promptLogin("LOGIN - FUNCIONARIO");

            String employeeName = context.getBusiness().authenticateEmployee(creds.username, creds.password);
            String role = context.getBusiness().getEmployeeRole(employeeName);

            if (!"STATION_WORKER".equals(role) && !"DELIVERY".equals(role) &&
                    !"MANAGER".equals(role) && !"COO".equals(role)) {
                UIHelper.showError("Acesso negado.");
                return;
            }

            context.setEmployeeName(employeeName);
            context.setRole(role);

            System.out.println("\n  Bem-vindo, " + employeeName + "!");

            showEmployeeWorkstationSelection();

        } catch (BusinessException e) {
            UIHelper.showError("Autenticacao falhou: " + e.getMessage());
        }
    }

    private void showEmployeeWorkstationSelection() {
        while (true) {
            try {
                Map<String, String> myStations = context.getBusiness()
                        .getAvailableWorkstationsForEmployee(context.getEmployeeName());

                if (myStations.isEmpty()) {
                    System.out.println("\n  Sem estacoes ativas no seu restaurante.");
                    return;
                }

                UIHelper.printHeader("SELECIONAR ESTACAO");
                System.out.println("  Utilizador: " + context.getEmployeeName());
                UIHelper.printDivider();

                List<String> stationCodes = new ArrayList<>(myStations.keySet());
                stationCodes.sort((s1, s2) -> {
                    int id1 = Integer.parseInt(s1.substring(3));
                    int id2 = Integer.parseInt(s2.substring(3));
                    return Integer.compare(id1, id2);
                });

                for (int i = 0; i < stationCodes.size(); i++) {
                    String code = stationCodes.get(i);
                    System.out.printf("  %d. %s%n", (i + 1), myStations.get(code));
                }

                int takeawayOption = stationCodes.size() + 1;
                int counterOption = stationCodes.size() + 2;
                System.out.println("  " + takeawayOption + ". Entregas (Takeaway)");
                System.out.println("  " + counterOption + ". Entregas (Balcao)");
                System.out.println("  0. Sair");
                UIHelper.printDivider();

                System.out.print("  Opcao: ");
                int choice = Menu.readIntSafe(new java.util.Scanner(System.in), 0, counterOption);

                if (choice == 0) {
                    System.out.println("\n  A terminar sessao...");
                    break;
                }

                String selectedCode = stationCodes.get(0);
                int wsId = Integer.parseInt(selectedCode.substring(3));
                int restaurantId = ((wsId - 1) / 5) + 1;

                if (choice == takeawayOption) {
                    DeliveryController controller = factory.createDeliveryController("TAKEAWAY", restaurantId);
                    Menu deliveryMenu = MenuBuilder.buildDeliveryMenu(controller);
                    deliveryMenu.run();
                } else if (choice == counterOption) {
                    DeliveryController controller = factory.createDeliveryController("DINE_IN", restaurantId);
                    Menu deliveryMenu = MenuBuilder.buildDeliveryMenu(controller);
                    deliveryMenu.run();
                } else {
                    selectedCode = stationCodes.get(choice - 1);
                    wsId = Integer.parseInt(selectedCode.substring(3));
                    restaurantId = ((wsId - 1) / 5) + 1;

                    EmployeeController controller = factory.createEmployeeController(
                            context.getEmployeeName(), selectedCode, restaurantId);
                    Menu employeeMenu = MenuBuilder.buildEmployeeMenu(controller);
                    employeeMenu.run();
                }

            } catch (BusinessException e) {
                UIHelper.showError(e.getMessage());
            }
        }
    }

    private void launchManagerInterface() {
        try {
            UIHelper.LoginCredentials creds = UIHelper.promptLogin("LOGIN - GESTOR");

            String employeeName = context.getBusiness().authenticateEmployee(creds.username, creds.password);
            String role = context.getBusiness().getEmployeeRole(employeeName);

            if (!"MANAGER".equals(role) && !"COO".equals(role)) {
                UIHelper.showError("Acesso restrito a gestores.");
                return;
            }

            context.setEmployeeName(employeeName);
            context.setRole(role);

            System.out.println("\n  Bem-vindo, " + employeeName + "!");

            int restaurantId = UIHelper.promptRestaurantSelection();

            ManagerController controller = factory.createManagerController(employeeName, restaurantId);
            Menu managerMenu = MenuBuilder.buildManagerMenu(controller);
            managerMenu.run();

        } catch (BusinessException e) {
            UIHelper.showError("Autenticacao falhou: " + e.getMessage());
        }
    }

    private void launchCOOInterface() {
        try {
            UIHelper.LoginCredentials creds = UIHelper.promptLogin("LOGIN - COO");

            String employeeName = context.getBusiness().authenticateEmployee(creds.username, creds.password);
            String role = context.getBusiness().getEmployeeRole(employeeName);

            if (!"COO".equals(role)) {
                UIHelper.showError("Acesso restrito ao COO.");
                return;
            }

            context.setEmployeeName(employeeName);
            context.setRole(role);

            System.out.println("\n  Bem-vindo, " + employeeName + "!");

            COOController controller = factory.createCOOController(employeeName);
            Menu cooMenu = MenuBuilder.buildCOOMenu(controller);
            cooMenu.run();

        } catch (BusinessException e) {
            UIHelper.showError("Autenticacao falhou: " + e.getMessage());
        }
    }
}
