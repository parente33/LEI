package uminho.dss.business.kitchen;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IKitchenSubsystem {

    String authenticateEmployee(String username, String password) throws KitchenException;

    String getEmployeeRole(String username) throws KitchenException;

    List<TaskInfo> getWorkstationTasks(String workstationCode) throws KitchenException;

    void completeTask(int taskId) throws KitchenException;

    void delayTask(int taskId, int delayMinutes, String reason) throws KitchenException;

    void reorderTasks(String workstationCode, List<Integer> taskIdOrder) throws KitchenException;

    void requestIngredient(int restaurantId, String ingredientName, double quantity) throws KitchenException;

    Set<String> getActiveWorkstations() throws KitchenException;

    Set<String> getActiveWorkstations(int restaurantId) throws KitchenException;

    void sendBroadcastMessage(String senderName, String messageText) throws KitchenException;

    void sendMessageToRestaurant(String senderName, int restaurantId, String messageText) throws KitchenException;

    Map<String, String> getRecentMessages(int limit) throws KitchenException;

    void generateTasksForOrder(String orderNumber) throws KitchenException;

    void startTask(int taskId) throws KitchenException;

    Map<String, Object> getOrderInfo(String orderNumber) throws KitchenException;

    void markOrderReady(String orderNumber) throws KitchenException;

    Map<String, String> getAvailableWorkstationsForEmployee(String username) throws KitchenException;
}
