package uminho.dss.business;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uminho.dss.business.order.*;
import uminho.dss.business.kitchen.IKitchenSubsystem;
import uminho.dss.business.kitchen.KitchenSubsystemFacade;
import uminho.dss.business.kitchen.TaskInfo;
import uminho.dss.business.analytics.AnalyticsSubsystemFacade;
import uminho.dss.business.analytics.IAnalyticsSubsystem;

public class BusinessFacade implements IBusiness {

    private IOrderSubsystem      orderSubsystem;
    private IKitchenSubsystem    kitchenSubsystem;
    private IAnalyticsSubsystem  analyticsSubsystem;

    public BusinessFacade() {
        this.orderSubsystem     = new OrderSubsystemFacade();
        this.kitchenSubsystem   = new KitchenSubsystemFacade();
        this.analyticsSubsystem = new AnalyticsSubsystemFacade();
    }

    /* ---- Order Subsystem ---- */

    @Override
    public Map<String, BigDecimal> getMenuPrices() throws BusinessException {
        return orderSubsystem.getMenuPrices();
    }

    @Override
    public Map<String, String> getMenuCategories() throws BusinessException {
        return orderSubsystem.getMenuCategories();
    }

    @Override
    public Set<String> getItemsByCategory(String category) throws BusinessException {
        return orderSubsystem.getItemsByCategory(category);
    }

    @Override
    public Set<String> getItemIngredients(String itemName) throws BusinessException {
        return orderSubsystem.getItemIngredients(itemName);
    }

    @Override
    public Set<String> getItemAllergens(String itemName) throws BusinessException {
        return orderSubsystem.getItemAllergens(itemName);
    }

    @Override
    public Map<String, Set<String>> getItemAlternatives(String itemName) throws BusinessException {
        return orderSubsystem.getItemAlternatives(itemName);
    }

    @Override
    public Set<String> filterMenuByAllergens(Set<String> excludeAllergens) throws BusinessException {
        return orderSubsystem.filterMenuByAllergens(excludeAllergens);
    }

    @Override
    public String createOrder(int kioskId, int restaurantId, String orderType, String paymentMethod) throws BusinessException {
        return orderSubsystem.createOrder(kioskId, restaurantId, orderType, paymentMethod);
    }

    @Override
    public void addItemToOrder(String orderNumber, String itemName, int quantity,
           String specialInstructions) throws BusinessException {
        orderSubsystem.addItemToOrder(orderNumber, itemName, quantity, specialInstructions);
    }

    @Override
    public void customizeItem(String orderNumber, String itemName, String removeIngredient,
           String addIngredient, String cookingInstruction) throws BusinessException {
        orderSubsystem.customizeItem(orderNumber, itemName, removeIngredient, addIngredient,
                cookingInstruction);
    }

    @Override
    public BigDecimal getOrderTotal(String orderNumber) throws BusinessException {
        return orderSubsystem.getOrderTotal(orderNumber);
    }

    @Override
    public void processPayment(String orderNumber) throws BusinessException {
        orderSubsystem.processPayment(orderNumber);
    }

    @Override
    public OrderReceipt finalizeOrder(String orderNumber) throws BusinessException {
        OrderReceipt receipt = orderSubsystem.finalizeOrder(orderNumber);
        kitchenSubsystem.generateTasksForOrder(orderNumber);
        return receipt;
    }

    @Override
    public String getOrderStatus(String orderNumber) throws BusinessException {
        return orderSubsystem.getOrderStatus(orderNumber);
    }

    @Override
    public LocalDateTime getOrderEstimatedTime(String orderNumber) throws BusinessException {
        return orderSubsystem.getOrderEstimatedTime(orderNumber);
    }

    @Override
    public void updateOrderStatus(String orderNumber, String status) throws BusinessException {
        orderSubsystem.updateOrderStatus(orderNumber, status);
    }

    @Override
    public void updateEstimatedTime(String orderNumber, LocalDateTime newTime) throws BusinessException {
        orderSubsystem.updateEstimatedTime(orderNumber, newTime);
    }
    
    @Override
    public int getCounterNumber(int counterId) throws BusinessException {
        return orderSubsystem.getCounterNumber(counterId);
    }

    /* ---- Kitchen Subsystem ---- */

    @Override
    public String authenticateEmployee(String username, String password) throws BusinessException {
        return kitchenSubsystem.authenticateEmployee(username, password);
    }

    @Override
    public String getEmployeeRole(String username) throws BusinessException {
        return kitchenSubsystem.getEmployeeRole(username);
    }

    @Override
    public List<TaskInfo> getWorkstationTasks(String workstationCode) throws BusinessException {
        return kitchenSubsystem.getWorkstationTasks(workstationCode);
    }

    @Override
    public void startTask(int taskId) throws BusinessException {
        kitchenSubsystem.startTask(taskId);
    }

    @Override
    public void completeTask(int taskId) throws BusinessException {
        kitchenSubsystem.completeTask(taskId);
    }

    @Override
    public void delayTask(int taskId, int delayMinutes, String reason) throws BusinessException {
        kitchenSubsystem.delayTask(taskId, delayMinutes, reason);
    }

    @Override
    public void reorderTasks(String workstationCode, List<Integer> taskIdOrder) throws BusinessException {
        kitchenSubsystem.reorderTasks(workstationCode, taskIdOrder);
    }

    @Override
    public void requestIngredient(int restaurantId, String ingredientName, double quantity) throws BusinessException {
        kitchenSubsystem.requestIngredient(restaurantId, ingredientName, quantity);
    }

    @Override
    public Set<String> getActiveWorkstations() throws BusinessException {
        return kitchenSubsystem.getActiveWorkstations();
    }

    @Override
    public Set<String> getActiveWorkstations(int restaurantId) throws BusinessException {
        return kitchenSubsystem.getActiveWorkstations(restaurantId);
    }

    @Override
    public void sendBroadcastMessage(String senderName, String messageText) throws BusinessException {
        kitchenSubsystem.sendBroadcastMessage(senderName, messageText);
    }

    @Override
    public void sendMessageToRestaurant(String senderName, int restaurantId, String messageText) throws BusinessException {
        kitchenSubsystem.sendMessageToRestaurant(senderName, restaurantId, messageText);
    }

    @Override
    public Map<String, String> getRecentMessages(int limit) throws BusinessException {
        return kitchenSubsystem.getRecentMessages(limit);
    }

    @Override
    public void generateTasksForOrder(String orderNumber) throws BusinessException {
        kitchenSubsystem.generateTasksForOrder(orderNumber);
    }

    @Override
    public Map<String, Object> getOrderInfo(String orderNumber) throws BusinessException {
        return kitchenSubsystem.getOrderInfo(orderNumber);
    }

    @Override
    public void markOrderReady(String orderNumber) throws BusinessException {
        kitchenSubsystem.markOrderReady(orderNumber);
    }

    /* ---- Analytics Subsystem ---- */

    @Override
    public int getTotalOrderCount(LocalDateTime start, LocalDateTime end) throws BusinessException {
        return analyticsSubsystem.getTotalOrderCount(start, end);
    }

    @Override
    public BigDecimal getTotalRevenue(LocalDateTime start, LocalDateTime end) throws BusinessException {
        return analyticsSubsystem.getTotalRevenue(start, end);
    }

    @Override
    public double getAverageServiceTime(LocalDateTime start, LocalDateTime end)
           throws BusinessException {
        return analyticsSubsystem.getAverageServiceTime(start, end);
    }

    @Override
    public Map<String, Integer> getBestSellingItems(int topN, LocalDateTime start,
           LocalDateTime end) throws BusinessException {
        return analyticsSubsystem.getBestSellingItems(topN, start, end);
    }

    @Override
    public Map<String, Integer> getWorkstationDemand(LocalDateTime start, LocalDateTime end)
           throws BusinessException {
        return analyticsSubsystem.getWorkstationDemand(start, end);
    }

    @Override
    public Map<String, Double> getLowStockIngredients() throws BusinessException {
        return analyticsSubsystem.getLowStockIngredients();
    }

    @Override
    public Map<String, Object> getRestaurantAnalytics(int restaurantId, LocalDateTime start, LocalDateTime end) 
           throws BusinessException {
        return analyticsSubsystem.getRestaurantAnalytics(restaurantId, start, end);
    }

    @Override
    public Map<String, String> getAvailableWorkstationsForEmployee(String username) throws BusinessException {
        return kitchenSubsystem.getAvailableWorkstationsForEmployee(username);
    }

    @Override
    public List<Order> getActiveOrders(int restaurantId) throws BusinessException {
        return orderSubsystem.getActiveOrders(restaurantId);
    }

    @Override
    public List<String> getOrderItems(String orderNumber) throws BusinessException {
        return orderSubsystem.getOrderItems(orderNumber);
    }

    @Override
    public void addOrderNote(String orderNumber, String note) throws BusinessException {
        try {
            this.orderSubsystem.addOrderNote(orderNumber, note);
        } catch (OrderException e) {
            throw new BusinessException(e.getMessage());
        }
    }
}
