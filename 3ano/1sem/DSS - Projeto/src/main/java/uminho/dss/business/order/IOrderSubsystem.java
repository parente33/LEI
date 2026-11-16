package uminho.dss.business.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IOrderSubsystem {

    Map<String, BigDecimal> getMenuPrices() throws OrderException;

    Map<String, String> getMenuCategories() throws OrderException;

    Set<String> getItemsByCategory(String category) throws OrderException;

    Set<String> getItemIngredients(String itemName) throws OrderException;

    Set<String> getItemAllergens(String itemName) throws OrderException;

    Map<String, Set<String>> getItemAlternatives(String itemName) throws OrderException;

    Set<String> filterMenuByAllergens(Set<String> excludeAllergens) throws OrderException;

    void addOrderNote(String orderNumber, String note) throws OrderException;

    List<String> getOrderItems(String orderNumber) throws OrderException;

    List<Order> getActiveOrders(int restaurantId) throws OrderException;

    String createOrder(int kioskId, int restaurantId, String orderType, String paymentMethod) throws OrderException;

    void addItemToOrder(String orderNumber, String itemName, int quantity,
        String specialInstructions) throws OrderException;

    void customizeItem(String orderNumber, String itemName, String removeIngredient,
        String addIngredient, String cookingInstruction) throws OrderException;

    BigDecimal getOrderTotal(String orderNumber) throws OrderException;

    void processPayment(String orderNumber) throws OrderException;

    OrderReceipt finalizeOrder(String orderNumber) throws OrderException;

    String getOrderStatus(String orderNumber) throws OrderException;

    LocalDateTime getOrderEstimatedTime(String orderNumber) throws OrderException;

    void updateOrderStatus(String orderNumber, String status) throws OrderException;

    void updateEstimatedTime(String orderNumber, LocalDateTime newTime) throws OrderException;
    
    int getCounterNumber(int counterId) throws OrderException;
}
