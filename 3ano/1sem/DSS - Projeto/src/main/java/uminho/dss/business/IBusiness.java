package uminho.dss.business;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uminho.dss.business.kitchen.TaskInfo;
import uminho.dss.business.order.Order;
import uminho.dss.business.order.OrderReceipt;

public interface IBusiness {

    /* ---- Order Subsystem ---- */

    Map<String, BigDecimal> getMenuPrices() throws BusinessException;

    Map<String, String> getMenuCategories() throws BusinessException;

    Set<String> getItemsByCategory(String category) throws BusinessException;

    Set<String> getItemIngredients(String itemName) throws BusinessException;

    Set<String> getItemAllergens(String itemName) throws BusinessException;

    Map<String, Set<String>> getItemAlternatives(String itemName) throws BusinessException;

    Set<String> filterMenuByAllergens(Set<String> excludeAllergens) throws BusinessException;

    String createOrder(int kioskId, int restaurantId, String orderType, String paymentMethod)
            throws BusinessException;

    void addItemToOrder(String orderNumber, String itemName, int quantity,
            String specialInstructions) throws BusinessException;

    void customizeItem(String orderNumber, String itemName, String removeIngredient,
            String addIngredient, String cookingInstruction) throws BusinessException;

    BigDecimal getOrderTotal(String orderNumber) throws BusinessException;

    void processPayment(String orderNumber) throws BusinessException;

    OrderReceipt finalizeOrder(String orderNumber) throws BusinessException;

    String getOrderStatus(String orderNumber) throws BusinessException;

    LocalDateTime getOrderEstimatedTime(String orderNumber) throws BusinessException;

    void updateOrderStatus(String orderNumber, String status) throws BusinessException;

    void updateEstimatedTime(String orderNumber, LocalDateTime newTime) throws BusinessException;

    int getCounterNumber(int counterId) throws BusinessException;

    /* ---- Kitchen Subsystem ---- */

    Map<String, String> getAvailableWorkstationsForEmployee(String username) throws BusinessException;

    List<Order> getActiveOrders(int restaurantId) throws BusinessException;

    String authenticateEmployee(String username, String password) throws BusinessException;

    String getEmployeeRole(String username) throws BusinessException;

    List<TaskInfo> getWorkstationTasks(String workstationCode) throws BusinessException;

    void startTask(int taskId) throws BusinessException;

    void completeTask(int taskId) throws BusinessException;

    void delayTask(int taskId, int delayMinutes, String reason) throws BusinessException;

    void reorderTasks(String workstationCode, List<Integer> taskIdOrder) throws BusinessException;

    void requestIngredient(int restaurantId, String ingredientName, double quantity)
            throws BusinessException;

    void addOrderNote(String orderNumber, String note) throws BusinessException;

    Set<String> getActiveWorkstations() throws BusinessException;

    List<String> getOrderItems(String orderNumber) throws BusinessException;

    Set<String> getActiveWorkstations(int restaurantId) throws BusinessException;

    void sendBroadcastMessage(String senderName, String messageText) throws BusinessException;

    void sendMessageToRestaurant(String senderName, int restaurantId, String messageText) throws BusinessException;

    Map<String, String> getRecentMessages(int limit) throws BusinessException;

    void generateTasksForOrder(String orderNumber) throws BusinessException;

    Map<String, Object> getOrderInfo(String orderNumber) throws BusinessException;

    void markOrderReady(String orderNumber) throws BusinessException;

    /* ---- Analytics Subsystem ---- */

    int getTotalOrderCount(LocalDateTime start, LocalDateTime end) throws BusinessException;

    BigDecimal getTotalRevenue(LocalDateTime start, LocalDateTime end) throws BusinessException;

    double getAverageServiceTime(LocalDateTime start, LocalDateTime end) throws BusinessException;

    Map<String, Integer> getBestSellingItems(int topN, LocalDateTime start, LocalDateTime end)
            throws BusinessException;

    Map<String, Integer> getWorkstationDemand(LocalDateTime start, LocalDateTime end)
            throws BusinessException;

    Map<String, Double> getLowStockIngredients() throws BusinessException;

    Map<String, Object> getRestaurantAnalytics(int restaurantId, LocalDateTime start, LocalDateTime end)
            throws BusinessException;
}
