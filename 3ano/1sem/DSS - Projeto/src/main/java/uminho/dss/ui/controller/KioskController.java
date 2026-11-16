package uminho.dss.ui.controller;
import uminho.dss.ui.view.*;
import uminho.dss.ui.UIException;

import uminho.dss.business.BusinessException;
import uminho.dss.business.IBusiness;
import uminho.dss.business.order.Order;
import uminho.dss.business.order.OrderReceipt;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class KioskController {

    private IBusiness model;
    private KioskView view;
    private int kioskId;
    private int restaurantId;
    private String currentOrderNumber;
    private Set<String> excludedAllergens;

    public KioskController(IBusiness model, int kioskId, int restaurantId) {
        this.model = model;
        this.view = new KioskView();
        this.kioskId = kioskId;
        this.restaurantId = restaurantId;
        this.excludedAllergens = new HashSet<>();
    }

    public void handleNewOrder() throws UIException {
        try {
            KioskView.OrderTypeData data = view.promptOrderType();
            currentOrderNumber = model.createOrder(kioskId, restaurantId, data.orderType, data.paymentMethod);
            view.displayOrderCreated(currentOrderNumber);

            handleOrderMenuLoop();

        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }

    private void handleOrderMenuLoop() throws UIException {
        boolean finished = false;
        while (!finished) {
            int option = view.showOrderMenu(currentOrderNumber);

            switch (option) {
                case 1 -> handleAddItem();
                case 2 -> handleCustomizeItem();
                case 3 -> handleAddNotes();
                case 4 -> handleViewTotal();
                case 5 -> {
                    handleFinalizeOrder();
                    finished = true;
                }
                case 0 -> finished = true;
            }
        }
    }

    private void handleAddItem() throws UIException {
        try {
            Map<String, String> categoriesMap = model.getMenuCategories();
            Set<String> availableCategories = new HashSet<>(categoriesMap.values());

            while (true) {
                String categoryCode = view.selectCategory(availableCategories, excludedAllergens);
                if (categoryCode == null)
                    return;

                Set<String> itemsSet = model.getItemsByCategory(categoryCode);

                if (!excludedAllergens.isEmpty()) {
                    Set<String> safeItems = model.filterMenuByAllergens(excludedAllergens);
                    itemsSet.retainAll(safeItems);
                }

                if (itemsSet.isEmpty()) {
                    view.displayNoItemsInCategory();
                    continue;
                }

                List<String> items = new ArrayList<>(itemsSet);
                Collections.sort(items);

                Map<String, BigDecimal> prices = model.getMenuPrices();
                Map<String, Set<String>> allergens = new HashMap<>();
                for (String item : items) {
                    allergens.put(item, model.getItemAllergens(item));
                }

                String categoryName = switch (categoryCode) {
                    case "MENU" -> "Menus";
                    case "MAIN" -> "Sanduiches e Refeicoes";
                    case "DRINK" -> "Bebidas";
                    case "SIDE" -> "Acompanhamentos";
                    default -> categoryCode;
                };

                KioskView.ItemSelection selection = view.selectItem(items, prices, allergens, categoryName);
                if (selection == null)
                    continue;

                model.addItemToOrder(currentOrderNumber, selection.itemName, selection.quantity, null);
                view.displayItemAdded(selection.itemName, selection.quantity);
                break;
            }

        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }

    private void handleCustomizeItem() throws UIException {
        try {
            List<String> currentItems = model.getOrderItems(currentOrderNumber);
            String selectedItem = view.selectItemToCustomize(currentItems);

            if (selectedItem == null)
                return;

            Map<String, Set<String>> alternatives = model.getItemAlternatives(selectedItem);
            view.displayAlternatives(alternatives);

            KioskView.CustomizationData data = view.promptCustomization();

            if (data.hasChanges()) {
                model.customizeItem(currentOrderNumber, selectedItem,
                        data.removeIngredient, data.addIngredient, data.cookingInstruction);
                view.displayCustomizationSuccess();
            } else {
                view.displayNoChanges();
            }

        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }

    private void handleAddNotes() throws UIException {
        try {
            String note = view.promptOrderNote();
            if (note != null) {
                model.addOrderNote(currentOrderNumber, note);
                view.displayNoteAdded();
            }
        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }

    private void handleViewTotal() throws UIException {
        try {
            BigDecimal total = model.getOrderTotal(currentOrderNumber);
            view.displayTotal(total);
        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }

    private void handleFinalizeOrder() throws UIException {
        try {
            BigDecimal total = model.getOrderTotal(currentOrderNumber);

            if (!view.confirmPayment(total)) {
                view.displayPaymentCancelled();
                return;
            }

            model.processPayment(currentOrderNumber);
            OrderReceipt receipt = model.finalizeOrder(currentOrderNumber);

            List<Order> orders = model.getActiveOrders(restaurantId);
            Integer counterNumber = null;
            String orderType = null;

            for (Order order : orders) {
                if (order.getOrderNumber().equals(receipt.getOrderNumber())) {
                    orderType = order.getOrderType();
                    if (order.getCounterId() != null) {
                        counterNumber = model.getCounterNumber(order.getCounterId());
                    }
                    break;
                }
            }

            view.displayReceipt(receipt.getOrderNumber(), receipt.getEstimatedCompletion(),
                    orderType, counterNumber);

            currentOrderNumber = null;

        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }

    public void handleFilterAllergens() {
        while (true) {
            String action = view.manageAllergenFilter(excludedAllergens);

            if ("EXIT".equals(action)) {
                return;
            } else if ("CLEAR_ALL".equals(action)) {
                excludedAllergens.clear();
                view.displayAllergensCleared();
            } else if (action != null) {
                if (excludedAllergens.contains(action)) {
                    excludedAllergens.remove(action);
                    view.displayAllergenToggled(action, false);
                } else {
                    excludedAllergens.add(action);
                    view.displayAllergenToggled(action, true);
                }
            }
        }
    }

    public void handleViewMenu() throws UIException {
        try {
            Map<String, BigDecimal> prices = model.getMenuPrices();
            Map<String, String> categories = model.getMenuCategories();

            Set<String> safeItems = null;
            if (!excludedAllergens.isEmpty()) {
                safeItems = model.filterMenuByAllergens(excludedAllergens);
            }

            Map<String, List<String>> byCategory = new LinkedHashMap<>();
            for (String item : prices.keySet()) {
                if (safeItems != null && !safeItems.contains(item))
                    continue;
                String cat = categories.get(item);
                byCategory.computeIfAbsent(cat, k -> new ArrayList<>()).add(item);
            }

            Map<String, Set<String>> allergens = new HashMap<>();
            for (String item : prices.keySet()) {
                allergens.put(item, model.getItemAllergens(item));
            }

            view.displayMenu(byCategory, prices, allergens, excludedAllergens);

        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }

    public void handleOrderStatus() throws UIException {
        try {
            List<Order> activeOrders = model.getActiveOrders(restaurantId);
            List<KioskView.OrderStatusInfo> orderInfo = new ArrayList<>();

            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

            for (Order order : activeOrders) {
                String statusDisplay = switch (order.getStatus()) {
                    case "PENDING" -> "A espera...";
                    case "IN_PROGRESS" -> "A preparar";
                    case "READY" -> "** PRONTO **";
                    default -> order.getStatus();
                };

                String timeDisplay = order.getEstimatedCompletionTime() != null
                        ? order.getEstimatedCompletionTime().format(timeFormat)
                        : "--:--";

                String counterDisplay;
                if (order.getOrderType().equals("TAKEAWAY")) {
                    counterDisplay = "Takeaway";
                } else if (order.getCounterId() != null) {
                    int counterNum = model.getCounterNumber(order.getCounterId());
                    counterDisplay = "Balcao " + counterNum;
                } else {
                    counterDisplay = "N/A";
                }

                orderInfo.add(new KioskView.OrderStatusInfo(
                        order.getOrderNumber(), statusDisplay, counterDisplay,
                        timeDisplay, order.getOrderItemIds().size()));
            }

            view.displayOrderStatus(orderInfo);

        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }
}
