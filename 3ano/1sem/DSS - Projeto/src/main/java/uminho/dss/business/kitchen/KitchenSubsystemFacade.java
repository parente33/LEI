package uminho.dss.business.kitchen;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import uminho.dss.business.order.Ingredient;
import uminho.dss.data.dao.*;
import uminho.dss.business.order.Order;
import uminho.dss.business.order.OrderItem;
import uminho.dss.business.order.MenuItem;

public class KitchenSubsystemFacade implements IKitchenSubsystem {

    private KitchenTaskDAO      kitchenTaskDAO;
    private EmployeeDAO         employeeDAO;
    private WorkstationDAO      workstationDAO;
    private EmployeeMessageDAO  messageDAO;
    private IngredientDAO       ingredientDAO;
    private MenuItemDAO         menuItemDAO;
    private InventoryItemDAO    inventoryDAO;
    private InventoryTransactionDAO transactionDAO;
    private OrderDAO            orderDAO;
    private OrderItemDAO        orderItemDAO;
    private WorkstationTypeDAO  workstationTypeDAO;

    public KitchenSubsystemFacade() {
        this.kitchenTaskDAO   = KitchenTaskDAO.getInstance();
        this.employeeDAO      = EmployeeDAO.getInstance();
        this.workstationDAO   = WorkstationDAO.getInstance();
        this.messageDAO       = EmployeeMessageDAO.getInstance();
        this.menuItemDAO      = MenuItemDAO.getInstance();
        this.ingredientDAO    = IngredientDAO.getInstance();
        this.inventoryDAO     = InventoryItemDAO.getInstance();
        this.transactionDAO   = InventoryTransactionDAO.getInstance();
        this.orderDAO         = OrderDAO.getInstance();
        this.orderItemDAO     = OrderItemDAO.getInstance();
        this.workstationTypeDAO = WorkstationTypeDAO.getInstance();
    }

    @Override
    public String authenticateEmployee(String username, String password) throws KitchenException {
        String authenticatedName = employeeDAO.authenticate(username, password);

        if (authenticatedName != null) {
            return authenticatedName;
        }

        throw new KitchenException("Authentication failed: Invalid username or password");
    }

    @Override
    public String getEmployeeRole(String username) throws KitchenException {
        Employee emp = findEmployeeByName(username);
        return emp.getRole();
    }

    @Override
    public List<TaskInfo> getWorkstationTasks(String workstationCode) throws KitchenException {
        Workstation station = findWorkstationByCode(workstationCode);
        List<TaskInfo> tasks = new ArrayList<>();

        for (KitchenTask task : kitchenTaskDAO.values()) {
            if (task.getWorkstationId() == station.getWorkstationId() &&
                    !task.getStatus().equals("COMPLETED")) {

                OrderItem orderItem = orderItemDAO.get(String.valueOf(task.getOrderItemId()));
                String orderNumber = "Unknown";
                String itemDescription = "";
                String orderNotes = "";

                if (orderItem != null) {
                    for (Order order : orderDAO.values()) {
                        if (order.getOrderItemIds().contains(task.getOrderItemId())) {
                            orderNumber = order.getOrderNumber();
                            orderNotes = order.getNotes();
                            break;
                        }
                    }
                    MenuItem menuItem = menuItemDAO.get(String.valueOf(orderItem.getMenuItemId()));
                    if (menuItem != null) {
                        itemDescription = menuItem.getName();
                    }
                }

                StringBuilder itemWithTaskNotes = new StringBuilder(itemDescription);
                if (task.getSpecialNotes() != null && !task.getSpecialNotes().isEmpty()) {
                    itemWithTaskNotes.append(" (").append(task.getSpecialNotes()).append(")");
                }

                String finalDescription = itemWithTaskNotes.toString();
                if (orderNotes != null && !orderNotes.isEmpty()) {
                    finalDescription += " | " + orderNotes;
                } else {
                    finalDescription += " | ---";
                }

                TaskInfo info = new TaskInfo(
                        task.getTaskId(),
                        orderNumber,
                        finalDescription,
                        task.getScheduledStartTime(),
                        task.getStatus(),
                        task.getPriority());
                tasks.add(info);
            }
        }

        tasks.sort((a, b) -> {
            int priorityCompare = Integer.compare(b.getPriority(), a.getPriority());
            if (priorityCompare != 0) return priorityCompare;
            if (a.getEstimatedTime() == null) return 1;
            if (b.getEstimatedTime() == null) return -1;
            return a.getEstimatedTime().compareTo(b.getEstimatedTime());
        });

        return tasks;
    }

    @Override
    public void startTask(int taskId) throws KitchenException {
        KitchenTask task = kitchenTaskDAO.get(String.valueOf(taskId));
        if (task == null) {
            throw new KitchenException("Task not found: " + taskId);
        }

        task.setStatus("IN_PROGRESS");
        task.setActualStartTime(LocalDateTime.now());
        kitchenTaskDAO.put(String.valueOf(taskId), task);
    }

    @Override
    public void completeTask(int taskId) throws KitchenException {
        KitchenTask task = kitchenTaskDAO.get(String.valueOf(taskId));
        if (task == null) {
            throw new KitchenException("Task not found");
        }

        task.setStatus("COMPLETED");
        task.setActualCompletionTime(LocalDateTime.now());
        kitchenTaskDAO.put(String.valueOf(taskId), task);

        checkAndUpdateOrderStatus(task.getOrderItemId());
    }

    private void checkAndUpdateOrderStatus(int orderItemId) {
        Order targetOrder = null;
        for (Order order : orderDAO.values()) {
            if (order.getOrderItemIds().contains(orderItemId)) {
                targetOrder = order;
                break;
            }
        }
        if (targetOrder == null) return;

        if ("READY".equals(targetOrder.getStatus()) || "COMPLETED".equals(targetOrder.getStatus())) {
            return;
        }
        Set<Integer> allOrderItemIds = new HashSet<>(targetOrder.getOrderItemIds());

        boolean allTasksComplete = true;
        int totalTasks = 0;
        int completedTasks = 0;
        
        for (KitchenTask task : kitchenTaskDAO.values()) {
            if (allOrderItemIds.contains(task.getOrderItemId())) {
                totalTasks++;
                if ("COMPLETED".equals(task.getStatus())) {
                    completedTasks++;
                } else {
                    allTasksComplete = false;
                }
            }
        }
        if (totalTasks > 0 && allTasksComplete) {
            targetOrder.setStatus("READY");
            targetOrder.setActualCompletionTime(LocalDateTime.now());
            orderDAO.put(String.valueOf(targetOrder.getOrderId()), targetOrder);
            System.out.println("\n  [SISTEMA] Pedido " + targetOrder.getOrderNumber() + " esta PRONTO!");
        }
    }

    @Override
    public void delayTask(int taskId, int delayMinutes, String reason) throws KitchenException {
        KitchenTask task = kitchenTaskDAO.get(String.valueOf(taskId));
        if (task == null) {
            throw new KitchenException("Task not found: " + taskId);
        }

        task.setStatus("DELAYED");
        LocalDateTime newScheduledTime = task.getScheduledStartTime() != null ? 
            task.getScheduledStartTime().plusMinutes(delayMinutes) : 
            LocalDateTime.now().plusMinutes(delayMinutes);
        task.setScheduledStartTime(newScheduledTime);
        
        task.setPriority(task.getPriority() + 1);
        
        kitchenTaskDAO.put(String.valueOf(taskId), task);

        updateOrderEstimatedTime(task.getOrderItemId(), delayMinutes);
        rebalanceQueueForWorkstation(task.getWorkstationId());
    }

    private void rebalanceQueueForWorkstation(int workstationId) {
        List<KitchenTask> pendingTasks = kitchenTaskDAO.values().stream()
            .filter(t -> t.getWorkstationId() == workstationId && 
                        (t.getStatus().equals("PENDING") || t.getStatus().equals("DELAYED")))
            .sorted(Comparator.comparingInt(KitchenTask::getPriority).reversed()
                    .thenComparing(t -> t.getScheduledStartTime() != null ? 
                        t.getScheduledStartTime() : LocalDateTime.MAX))
            .collect(Collectors.toList());

        LocalDateTime nextAvailable = LocalDateTime.now();
        for (KitchenTask task : pendingTasks) {
            if (task.getStatus().equals("DELAYED")) {
                continue;
            }
            task.setScheduledStartTime(nextAvailable);
            int duration = task.getEstimatedDuration() != null ? task.getEstimatedDuration() : 10;
            nextAvailable = nextAvailable.plusMinutes(duration);
            kitchenTaskDAO.put(String.valueOf(task.getTaskId()), task);
        }
    }

    private void updateOrderEstimatedTime(int orderItemId, int additionalMinutes) {
        for (Order order : orderDAO.values()) {
            if (order.getOrderItemIds().contains(orderItemId)) {
                LocalDateTime current = order.getEstimatedCompletionTime();
                if (current != null) {
                    order.setEstimatedCompletionTime(current.plusMinutes(additionalMinutes));
                    orderDAO.put(String.valueOf(order.getOrderId()), order);
                }
                break;
            }
        }
    }

    @Override
    public void reorderTasks(String workstationCode, List<Integer> taskIdOrder) throws KitchenException {
        Workstation station = findWorkstationByCode(workstationCode);
        
        for (int taskId : taskIdOrder) {
            KitchenTask task = kitchenTaskDAO.get(String.valueOf(taskId));
            if (task == null) {
                throw new KitchenException("Task not found: " + taskId);
            }
            if (task.getWorkstationId() != station.getWorkstationId()) {
                throw new KitchenException("Task " + taskId + " does not belong to workstation " + workstationCode);
            }
        }

        LocalDateTime nextTime = LocalDateTime.now();
        int maxPriority = taskIdOrder.size();
        
        for (int i = 0; i < taskIdOrder.size(); i++) {
            int taskId = taskIdOrder.get(i);
            KitchenTask task = kitchenTaskDAO.get(String.valueOf(taskId));
            
            task.setPriority(maxPriority - i);
            task.setScheduledStartTime(nextTime);
            
            int duration = task.getEstimatedDuration() != null ? task.getEstimatedDuration() : 10;
            nextTime = nextTime.plusMinutes(duration);
            
            kitchenTaskDAO.put(String.valueOf(taskId), task);
        }
    }

    @Override
    public void requestIngredient(int restaurantId, String ingredientName, double quantity) throws KitchenException {
        Ingredient ingredient = findIngredientByName(ingredientName);
        if (ingredient == null) {
            throw new KitchenException("Ingredient not found: " + ingredientName);
        }

        InventoryItem inventoryItem = inventoryDAO.getByCompositeKey(restaurantId, ingredient.getIngredientId());
        if (inventoryItem == null) {
            throw new KitchenException("Inventory item not found for ingredient: " + ingredientName);
        }

        if (inventoryItem.getWarehouseQuantity().doubleValue() < quantity) {
            throw new KitchenException("Insufficient warehouse stock for: " + ingredientName);
        }

        inventoryDAO.transferFromWarehouse(restaurantId, ingredient.getIngredientId(), BigDecimal.valueOf(quantity));

        int transactionId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        InventoryTransaction transaction = new InventoryTransaction(
            transactionId,
            restaurantId,
            ingredient.getIngredientId(),
            "WAREHOUSE_TO_KITCHEN",
            BigDecimal.valueOf(quantity),
            LocalDateTime.now(),
            "Requested by kitchen staff",
            null
        );
        transactionDAO.put(String.valueOf(transactionId), transaction);
    }

    @Override
    public Set<String> getActiveWorkstations() throws KitchenException {
        Set<String> codes = new HashSet<>();

        for (Workstation station : workstationDAO.values()) {
            if (station.isActive()) {
                codes.add("WS-" + station.getWorkstationId());
            }
        }

        return codes;
    }

    @Override
    public Set<String> getActiveWorkstations(int restaurantId) throws KitchenException {
        Set<String> codes = new HashSet<>();

        for (Workstation station : workstationDAO.values()) {
            if (station.isActive() && station.getRestaurantId() == restaurantId) {
                codes.add("WS-" + station.getWorkstationId());
            }
        }

        return codes;
    }

    @Override
    public void sendBroadcastMessage(String senderName, String messageText) throws KitchenException {
        Employee sender = findEmployeeByName(senderName);

        int messageId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        EmployeeMessage msg = new EmployeeMessage(
                messageId,
                sender.getEmployeeId(),
                messageText,
                LocalDateTime.now());

        messageDAO.put(String.valueOf(messageId), msg);
    }

    @Override
    public void sendMessageToRestaurant(String senderName, int restaurantId, String messageText) throws KitchenException {
        Employee sender = findEmployeeByName(senderName);

        String prefixedMessage = "[Restaurant " + restaurantId + "] " + messageText;
        
        int messageId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        EmployeeMessage msg = new EmployeeMessage(
                messageId,
                sender.getEmployeeId(),
                prefixedMessage,
                LocalDateTime.now());

        messageDAO.put(String.valueOf(messageId), msg);
    }

    @Override
    public Map<String, String> getRecentMessages(int limit) throws KitchenException {
        Map<String, String> messages = new LinkedHashMap<>();

        List<EmployeeMessage> allMessages = new ArrayList<>(messageDAO.values());
        allMessages.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        int count = 0;
        for (EmployeeMessage msg : allMessages) {
            if (count >= limit)
                break;

            Employee sender = employeeDAO.get(String.valueOf(msg.getSenderEmployeeId()));
            String senderName = sender != null ? sender.getName() : "Unknown";

            messages.put(msg.getCreatedAt().toString() + ": " + msg.getMessageText(), senderName);
            count++;
        }

        return messages;
    }

    @Override
    public void generateTasksForOrder(String orderNumber) throws KitchenException {
        Order order = findOrderByNumber(orderNumber);
        if (order == null) {
            throw new KitchenException("Order not found: " + orderNumber);
        }

        List<OrderItem> orderItems = getOrderItems(order);
        if (orderItems.isEmpty()) return;

        LocalDateTime parallelStartTime = LocalDateTime.now();
        LocalDateTime latestEndTime = parallelStartTime;

        for (OrderItem item : orderItems) {
            MenuItem menuItem = menuItemDAO.get(String.valueOf(item.getMenuItemId()));
            if (menuItem == null) continue;

            Map<Integer, List<Ingredient>> ingredientsByStation = groupIngredientsByStation(menuItem);

            for (Map.Entry<Integer, List<Ingredient>> entry : ingredientsByStation.entrySet()) {
                int stationTypeId = entry.getKey();
                List<Ingredient> ingredients = entry.getValue();

                LocalDateTime taskEndTime = createParallelKitchenTask(
                    order, item, stationTypeId, ingredients, 
                    order.getRestaurantId(), parallelStartTime
                );

                if (taskEndTime.isAfter(latestEndTime)) {
                    latestEndTime = taskEndTime;
                }
            }
        }

        scheduleAssemblyTask(order, orderItems.get(0).getOrderItemId(), latestEndTime);
    }

    private List<OrderItem> getOrderItems(Order order) {
        List<OrderItem> items = new ArrayList<>();
        for (int id : order.getOrderItemIds()) {
            OrderItem oi = orderItemDAO.get(String.valueOf(id));
            if (oi != null) items.add(oi);
        }
        return items;
    }

    private LocalDateTime createParallelKitchenTask(Order order, OrderItem item, int stationTypeId,
                                                    List<Ingredient> ingredients, int restaurantId,
                                                    LocalDateTime startTime) {

        Workstation station = findPhysicalStation(restaurantId, stationTypeId);
        if (station == null) return startTime;

        int duration = Math.max(5, ingredients.size() * 5);
        LocalDateTime end = startTime.plusMinutes(duration);

        String ingredientList = ingredients.stream()
                .map(Ingredient::getName)
                .collect(Collectors.joining(", "));

        String description = "Preparar: " + ingredientList;
        if (item.getSpecialInstructions() != null && !item.getSpecialInstructions().isEmpty()) {
            description += " | Nota: " + item.getSpecialInstructions();
        }

        int taskId = generateUniqueTaskId();
        KitchenTask task = new KitchenTask(
                taskId, item.getOrderItemId(), station.getWorkstationId(),
                "PENDING", order.getPriority(), duration, startTime, null, null, description
        );
        kitchenTaskDAO.put(String.valueOf(taskId), task);

        return end;
    }

    private void scheduleAssemblyTask(Order order, int representativeItemId, LocalDateTime startTime) {
        int assemblyTypeId = findTypeIdByName("ASSEMBLY");

        if (assemblyTypeId == -1) {
            System.out.println("Tipo de estacao 'ASSEMBLY' nao encontrado.");
            return;
        }

        Workstation assemblyStation = findPhysicalStation(order.getRestaurantId(), assemblyTypeId);

        if (assemblyStation != null) {
            int taskId = generateUniqueTaskId();
            int duration = 5;

            LocalDateTime estimatedCompletion = startTime.plusMinutes(duration);

            KitchenTask assemblyTask = new KitchenTask(
                    taskId,
                    representativeItemId,
                    assemblyStation.getWorkstationId(),
                    "PENDING",
                    order.getPriority(),
                    duration,
                    startTime,
                    null, null,
                    "MONTAGEM FINAL: Pedido " + order.getOrderNumber()
            );
            kitchenTaskDAO.put(String.valueOf(taskId), assemblyTask);
            order.setEstimatedCompletionTime(estimatedCompletion);
            orderDAO.put(String.valueOf(order.getOrderId()), order);
        }
    }

    private Map<Integer, List<Ingredient>> groupIngredientsByStation(MenuItem item) {
        Map<Integer, List<Ingredient>> map = new HashMap<>();
        for (Integer ingId : item.getIngredientIds()) {
            Ingredient ing = ingredientDAO.get(String.valueOf(ingId));
            if (ing != null && ing.getWorkstationTypeId() > 0) {
                map.computeIfAbsent(ing.getWorkstationTypeId(), k -> new ArrayList<>()).add(ing);
            }
        }
        return map;
    }

    private int generateUniqueTaskId() {
        return Math.abs((int) (System.nanoTime()) % Integer.MAX_VALUE);
    }

    @Override
    public Map<String, Object> getOrderInfo(String orderNumber) throws KitchenException {
        Order order = findOrderByNumber(orderNumber);
        if (order == null) {
            throw new KitchenException("Order not found: " + orderNumber);
        }

        Map<String, Object> info = new HashMap<>();
        info.put("orderNumber", order.getOrderNumber());
        info.put("status", order.getStatus());
        info.put("orderType", order.getOrderType());
        info.put("estimatedTime", order.getEstimatedCompletionTime());
        info.put("totalPrice", order.getTotalPrice());
        int totalTasks = 0;
        int completedTasks = 0;
        for (int itemId : order.getOrderItemIds()) {
            for (KitchenTask task : kitchenTaskDAO.values()) {
                if (task.getOrderItemId() == itemId) {
                    totalTasks++;
                    if (task.getStatus().equals("COMPLETED")) {
                        completedTasks++;
                    }
                }
            }
        }
        info.put("totalTasks", totalTasks);
        info.put("completedTasks", completedTasks);

        return info;
    }

    @Override
    public void markOrderReady(String orderNumber) throws KitchenException {
        Order order = findOrderByNumber(orderNumber);
        if (order == null) {
            throw new KitchenException("Order not found: " + orderNumber);
        }

        order.setStatus("READY");
        orderDAO.put(String.valueOf(order.getOrderId()), order);
    }

    private Workstation findPhysicalStation(int restaurantId, int typeId) {
        for (Workstation ws : workstationDAO.values()) {
            if (ws.getRestaurantId() == restaurantId &&
                    ws.getWorkstationTypeId() == typeId &&
                    ws.isActive()) {
                return ws;
            }
        }
        return null;
    }

    private LocalDateTime getNextAvailableTime(int workstationId) {
        List<KitchenTask> tasks = new ArrayList<>(kitchenTaskDAO.values());
        LocalDateTime lastEndTime = LocalDateTime.now();

        for (KitchenTask task : tasks) {
            if (task.getWorkstationId() == workstationId &&
                    (task.getStatus().equals("PENDING") || task.getStatus().equals("IN_PROGRESS"))) {

                LocalDateTime taskEnd;
                if (task.getActualCompletionTime() != null) {
                    taskEnd = task.getActualCompletionTime();
                } else {
                    LocalDateTime start = task.getActualStartTime() != null ? 
                        task.getActualStartTime() : task.getScheduledStartTime();
                    if (start == null) start = LocalDateTime.now();

                    int duration = task.getEstimatedDuration() != null ? task.getEstimatedDuration() : 10;
                    taskEnd = start.plusMinutes(duration);
                }

                if (taskEnd.isAfter(lastEndTime)) {
                    lastEndTime = taskEnd;
                }
            }
        }
        return lastEndTime;
    }

    private Employee findEmployeeByName(String name) throws KitchenException {
        for (Employee emp : employeeDAO.values()) {
            if (emp.getName().equals(name)) {
                return emp;
            }
        }
        throw new KitchenException("Employee not found: " + name);
    }

    private Workstation findWorkstationByCode(String code) throws KitchenException {
        if (!code.startsWith("WS-")) {
            throw new KitchenException("Invalid workstation code format: " + code);
        }
        String idStr = code.substring(3);
        Workstation station = workstationDAO.get(idStr);
        if (station == null) {
            throw new KitchenException("Workstation not found: " + code);
        }
        return station;
    }

    private Order findOrderByNumber(String orderNumber) throws KitchenException {
        for (Order order : orderDAO.values()) {
            if (order.getOrderNumber().equals(orderNumber)) {
                return order;
            }
        }
        throw new KitchenException("Order not found: " + orderNumber);
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
    public Map<String, String> getAvailableWorkstationsForEmployee(String username) throws KitchenException {
        Employee emp = findEmployeeByName(username);
        int restaurantId = emp.getRestaurantId();
        Map<String, String> availableStations = new java.util.LinkedHashMap<>();
        
        for (Workstation ws : workstationDAO.values()) {
            if (ws.getRestaurantId() == restaurantId && ws.isActive()) {
                WorkstationType type = workstationTypeDAO.get(String.valueOf(ws.getWorkstationTypeId()));
                
                if (type != null && "DELIVERY".equals(type.getName())) {
                    continue;
                }
                
                String code = "WS-" + ws.getWorkstationId();
                
                String typeName;
                if (type != null) {
                    typeName = switch (type.getName()) {
                        case "GRILL" -> "Grelha";
                        case "FRYER" -> "Fritadeira";
                        case "EGG_STATION" -> "Estacao Ovos";
                        case "VEGETABLE_PREP" -> "Prep. Vegetais";
                        case "ASSEMBLY" -> "Montagem";
                        default -> type.getName();
                    };
                } else {
                    typeName = "Estacao Desconhecida";
                }
                
                availableStations.put(code, typeName);
            }
        }
        return availableStations;
    }

    private int findTypeIdByName(String name) {
        for (WorkstationType type : workstationTypeDAO.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type.getId();
            }
        }
        return -1;
    }
}
