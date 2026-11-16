package uminho.dss.business.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import uminho.dss.data.dao.IngredientDAO;
import uminho.dss.data.dao.MenuItemDAO;
import uminho.dss.data.dao.OrderDAO;
import uminho.dss.data.dao.OrderItemDAO;
import uminho.dss.data.dao.CustomizationDAO;

public class OrderSubsystemFacade implements IOrderSubsystem {

    private IngredientDAO     ingredientDAO;
    private MenuItemDAO       menuItemDAO;
    private OrderDAO          orderDAO;
    private OrderItemDAO      orderItemDAO;
    private CustomizationDAO  customizationDAO;

    private static int orderCounter = 1000;

    public OrderSubsystemFacade() {
        this.ingredientDAO     = IngredientDAO.getInstance();
        this.menuItemDAO       = MenuItemDAO.getInstance();
        this.orderDAO          = OrderDAO.getInstance();
        this.orderItemDAO      = OrderItemDAO.getInstance();
        this.customizationDAO  = CustomizationDAO.getInstance();
        int maxId = 1000;
        for (Order order : orderDAO.values()) {
            try {
                String numStr = order.getOrderNumber().replace("#", "");
                int num = Integer.parseInt(numStr);
                if (num > maxId) {
                    maxId = num;
                }
            } catch (NumberFormatException e) {
            }
        }
        if (maxId > orderCounter) {
            orderCounter = maxId;
        }
    }

    @Override
    public Map<String, BigDecimal> getMenuPrices() throws OrderException {
        Map<String, BigDecimal> prices = new HashMap<>();

        for (MenuItem item : menuItemDAO.values()) {
            if (item.isActive()) {
                prices.put(item.getName(), item.getBasePrice());
            }
        }

        return prices;
    }

    @Override
    public Map<String, String> getMenuCategories() throws OrderException {
        Map<String, String> categories = new HashMap<>();

        for (MenuItem item : menuItemDAO.values()) {
            if (item.isActive()) {
                categories.put(item.getName(), item.getCategory());
            }
        }

        return categories;
    }

    @Override
    public Set<String> getItemsByCategory(String category) throws OrderException {
        return menuItemDAO.values().stream()
                .filter(item -> item.isActive() && category.equals(item.getCategory()))
                .map(MenuItem::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getItemIngredients(String itemName) throws OrderException {
        MenuItem item = findMenuItemByName(itemName);

        Set<String> ingredientNames = new HashSet<>();
        for (Integer ingredientId : item.getIngredientIds()) {
            Ingredient ing = ingredientDAO.get(String.valueOf(ingredientId));
            if (ing != null) {
                ingredientNames.add(ing.getName());
            }
        }
        return ingredientNames;
    }

    @Override
    public Set<String> getItemAllergens(String itemName) throws OrderException {
        MenuItem item = findMenuItemByName(itemName);

        Set<String> allergens = new HashSet<>();
        for (Integer ingredientId : item.getIngredientIds()) {
            Ingredient ing = ingredientDAO.get(String.valueOf(ingredientId));
            if (ing != null && ing.getAllergenType() != null) {
                allergens.add(ing.getAllergenType());
            }
        }
        return allergens;
    }

    @Override
    public Map<String, Set<String>> getItemAlternatives(String itemName) throws OrderException {
        MenuItem item = findMenuItemByName(itemName);
        Map<String, Set<String>> alternatives = new HashMap<>();
        for (Integer ingredientId : item.getIngredientIds()) {
            Ingredient original = ingredientDAO.get(String.valueOf(ingredientId));
            if (original == null) continue;
            Map<Integer, BigDecimal> alts = item.getAlternativesFor(ingredientId);
            if (!alts.isEmpty()) {
                Set<String> altNames = new HashSet<>();
                for (Integer altId : alts.keySet()) {
                    Ingredient altIng = ingredientDAO.get(String.valueOf(altId));
                    if (altIng != null) {
                        altNames.add(altIng.getName());
                    }
                }
                if (!altNames.isEmpty()) {
                    alternatives.put(original.getName(), altNames);
                }
            }
        }

        return alternatives;
    }

    @Override
    public Set<String> filterMenuByAllergens(Set<String> excludeAllergens) throws OrderException {
        Set<String> validItems = new HashSet<>();

        for (MenuItem item : menuItemDAO.values()) {
            if (!item.isActive())
                continue;

            boolean hasExcludedAllergen = false;
            for (Integer ingredientId : item.getIngredientIds()) {
                Ingredient ing = ingredientDAO.get(String.valueOf(ingredientId));
                if (ing != null && excludeAllergens.contains(ing.getAllergenType())) {
                    hasExcludedAllergen = true;
                    break;
                }
            }
            if (!hasExcludedAllergen) {
                validItems.add(item.getName());
            }
        }

        return validItems;
    }

    @Override
    public String createOrder(int kioskId, int restaurantId, String orderType, String paymentMethod) throws OrderException {
        try {
            int orderId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
            String orderNumber = "#" + (++orderCounter);
            Order order = new Order(
                    orderId,
                    orderNumber,
                    kioskId,
                    restaurantId,
                    LocalDateTime.now(),
                    orderType,
                    "PENDING",
                    BigDecimal.ZERO,
                    paymentMethod,
                    "PENDING",
                    LocalDateTime.now().plusMinutes(15),
                    null);
            
            if (!"TAKEAWAY".equals(orderType)) {
                int counterId = orderDAO.assignCounter(restaurantId);
                order.setCounterId(counterId);
            }
            
            orderDAO.put(String.valueOf(orderId), order);
            return orderNumber;
        } catch (Exception e) {
            throw new OrderException("Failed to create order: " + e.getMessage());
        }
    }

    @Override
    public void addItemToOrder(String orderNumber, String itemName, int quantity,
                               String specialInstructions) throws OrderException {
        Order order = findOrderByNumber(orderNumber);
        MenuItem item = findMenuItemByName(itemName);

        int orderItemId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        OrderItem orderItem = new OrderItem(
                orderItemId,
                order.getOrderId(),
                item.getMenuItemId(),
                quantity,
                item.getBasePrice(),
                specialInstructions);

        orderItemDAO.put(String.valueOf(orderItemId), orderItem);
        order.addOrderItem(orderItemId);
        BigDecimal itemTotal = item.getBasePrice().multiply(BigDecimal.valueOf(quantity));
        order.setTotalPrice(order.getTotalPrice().add(itemTotal));

        orderDAO.put(String.valueOf(order.getOrderId()), order);
    }

    @Override
    public void customizeItem(String orderNumber, String itemName, String removeIngredient,
                              String addIngredient, String cookingInstruction) throws OrderException {

        Order order = findOrderByNumber(orderNumber);
        int targetOrderItemId = -1;
        List<Integer> itemIds = order.getOrderItemIds();
        for (int i = itemIds.size() - 1; i >= 0; i--) {
            int id = itemIds.get(i);
            OrderItem oi = orderItemDAO.get(String.valueOf(id));
            if (oi == null) continue;
            MenuItem mi = menuItemDAO.get(String.valueOf(oi.getMenuItemId()));
            if (mi != null && mi.getName().equals(itemName)) {
                targetOrderItemId = id;
                break;
            }
        }

        if (targetOrderItemId == -1) {
            throw new OrderException("Item " + itemName + " not found in order " + orderNumber);
        }

        OrderItem orderItem = orderItemDAO.get(String.valueOf(targetOrderItemId));

        if (cookingInstruction != null && !cookingInstruction.isEmpty()) {
            int custId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
            Customization cust = new Customization(
                    custId,
                    targetOrderItemId,
                    "COOKING_METHOD",
                    null,
                    null,
                    cookingInstruction);
            customizationDAO.put(String.valueOf(custId), cust);
            orderItem.addCustomization(custId);
        }

        if (removeIngredient != null && !removeIngredient.isEmpty()) {
            Ingredient ingRemove = findIngredientByName(removeIngredient);
            if (ingRemove != null) {
                int custId = (int) ((System.currentTimeMillis() + 1) % Integer.MAX_VALUE);
                Customization cust = new Customization(
                        custId,
                        targetOrderItemId,
                        "REMOVE_INGREDIENT",
                        ingRemove.getIngredientId(),
                        null,
                        null);
                customizationDAO.put(String.valueOf(custId), cust);
                orderItem.addCustomization(custId);
            }
        }

        if (addIngredient != null && !addIngredient.isEmpty()) {
            Ingredient ingAdd = findIngredientByName(addIngredient);
            if (ingAdd != null) {
                int custId = (int) ((System.currentTimeMillis() + 2) % Integer.MAX_VALUE);
                String type = (removeIngredient != null && !removeIngredient.isEmpty()) ?
                        "SUBSTITUTE_INGREDIENT" : "ADD_INGREDIENT";

                Integer removeId = null;
                if (removeIngredient != null && !removeIngredient.isEmpty()) {
                    Ingredient ingRemove = findIngredientByName(removeIngredient);
                    if (ingRemove != null) removeId = ingRemove.getIngredientId();
                }

                Customization cust = new Customization(
                        custId,
                        targetOrderItemId,
                        type,
                        removeId != null ? removeId : ingAdd.getIngredientId(),
                        removeId != null ? ingAdd.getIngredientId() : null,
                        null);
                customizationDAO.put(String.valueOf(custId), cust);
                orderItem.addCustomization(custId);
            }
        }

        orderItemDAO.put(String.valueOf(targetOrderItemId), orderItem);
    }

    @Override
    public BigDecimal getOrderTotal(String orderNumber) throws OrderException {
        Order order = findOrderByNumber(orderNumber);
        return order.getTotalPrice();
    }

    @Override
    public void processPayment(String orderNumber) throws OrderException {
        Order order = findOrderByNumber(orderNumber);
        order.setPaymentStatus("PAID");
        order.setStatus("IN_PROGRESS");
        orderDAO.put(String.valueOf(order.getOrderId()), order);
    }

    @Override
    public OrderReceipt finalizeOrder(String orderNumber) throws OrderException {
        Order order = findOrderByNumber(orderNumber);

        order.setStatus("IN_PROGRESS");
        long minutes = 5 + (order.getOrderItemIds().size() * 3L);
        LocalDateTime eta = LocalDateTime.now().plusMinutes(minutes);
        order.setEstimatedCompletionTime(eta);

        orderDAO.put(String.valueOf(order.getOrderId()), order);

        return new OrderReceipt(
                order.getOrderNumber(),
                order.getTotalPrice(),
                order.getEstimatedCompletionTime(),
                order.getOrderType());
    }

    @Override
    public String getOrderStatus(String orderNumber) throws OrderException {
        Order order = findOrderByNumber(orderNumber);
        return order.getStatus();
    }

    @Override
    public LocalDateTime getOrderEstimatedTime(String orderNumber) throws OrderException {
        Order order = findOrderByNumber(orderNumber);
        return order.getEstimatedCompletionTime();
    }

    @Override
    public void updateOrderStatus(String orderNumber, String status) throws OrderException {
        Order order = findOrderByNumber(orderNumber);
        order.setStatus(status);
        if ("COMPLETED".equals(status)) {
            order.setActualCompletionTime(LocalDateTime.now());
        }
        orderDAO.put(String.valueOf(order.getOrderId()), order);
    }

    @Override
    public void updateEstimatedTime(String orderNumber, LocalDateTime newTime) throws OrderException {
        Order order = findOrderByNumber(orderNumber);
        order.setEstimatedCompletionTime(newTime);
        orderDAO.put(String.valueOf(order.getOrderId()), order);
    }

    @Override
    public List<Order> getActiveOrders(int restaurantId) throws OrderException {
        List<Order> activeOrders = new java.util.ArrayList<>();

        for (Order order : orderDAO.values()) {
            if (order.getRestaurantId() == restaurantId) {
                String s = order.getStatus();
                if ("PENDING".equals(s) || "IN_PROGRESS".equals(s) || "READY".equals(s)) {
                    activeOrders.add(order);
                }
            }
        }
        activeOrders.sort(java.util.Comparator.comparing(Order::getOrderDatetime));
        return activeOrders;
    }
    
    @Override
    public int getCounterNumber(int counterId) throws OrderException {
        return orderDAO.getCounterNumber(counterId);
    }

    private MenuItem findMenuItemByName(String name) throws OrderException {
        for (MenuItem item : menuItemDAO.values()) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        throw new OrderException("Menu item not found: " + name);
    }

    private Order findOrderByNumber(String orderNumber) throws OrderException {
        for (Order order : orderDAO.values()) {
            if (order.getOrderNumber().equals(orderNumber)) {
                return order;
            }
        }
        throw new OrderException("Order not found: " + orderNumber);
    }

    private Ingredient findIngredientByName(String name) {
        for (Ingredient ing : ingredientDAO.values()) {
            if (ing.getName().equalsIgnoreCase(name)) {
                return ing;
            }
        }
        return null;
    }

    @Override
    public List<String> getOrderItems(String orderNumber) throws OrderException {
        Order targetOrder = null;
        for (Order o : orderDAO.values()) {
            if (o.getOrderNumber().equals(orderNumber)) {
                targetOrder = o;
                break;
            }
        }
        if (targetOrder == null) {
            throw new OrderException("Order not found: " + orderNumber);
        }

        List<String> itemNames = new ArrayList<>();
        for (int itemId : targetOrder.getOrderItemIds()) {
            OrderItem item = orderItemDAO.get(String.valueOf(itemId));
            if (item != null) {
                MenuItem menuItem = menuItemDAO.get(String.valueOf(item.getMenuItemId()));
                if (menuItem != null) {
                    itemNames.add(menuItem.getName());
                }
            }
        }
        return itemNames;
    }

    @Override
    public void addOrderNote(String orderNumber, String note) throws OrderException {
        Order order = findOrderByNumber(orderNumber);
        if (order == null) throw new OrderException("Order not found");

        String current = order.getNotes();
        if (current == null || current.isEmpty()) {
            order.setNotes(note);
        } else {
            order.setNotes(current + " | " + note);
        }
        orderDAO.put(String.valueOf(order.getOrderId()), order);
    }
}
