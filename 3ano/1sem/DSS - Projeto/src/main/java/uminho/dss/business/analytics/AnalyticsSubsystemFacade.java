package uminho.dss.business.analytics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import uminho.dss.business.kitchen.Workstation;
import uminho.dss.business.kitchen.WorkstationType;
import uminho.dss.business.order.Order;
import uminho.dss.business.order.OrderItem;
import uminho.dss.business.order.MenuItem;
import uminho.dss.business.order.Ingredient;
import uminho.dss.business.kitchen.KitchenTask;
import uminho.dss.business.kitchen.InventoryItem;
import uminho.dss.data.dao.*;

public class AnalyticsSubsystemFacade implements IAnalyticsSubsystem {

    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;
    private MenuItemDAO menuItemDAO;
    private KitchenTaskDAO kitchenTaskDAO;
    private InventoryItemDAO inventoryDAO;
    private IngredientDAO ingredientDAO;
    private WorkstationDAO workstationDAO;
    private WorkstationTypeDAO  workstationTypeDAO;

    public AnalyticsSubsystemFacade() {
        this.orderDAO = OrderDAO.getInstance();
        this.orderItemDAO = OrderItemDAO.getInstance();
        this.menuItemDAO = MenuItemDAO.getInstance();
        this.kitchenTaskDAO = KitchenTaskDAO.getInstance();
        this.inventoryDAO = InventoryItemDAO.getInstance();
        this.ingredientDAO = IngredientDAO.getInstance();
        this.workstationDAO = WorkstationDAO.getInstance();
        this.workstationTypeDAO = WorkstationTypeDAO.getInstance();
    }

    @Override
    public int getTotalOrderCount(LocalDateTime start, LocalDateTime end) throws AnalyticsException {
        int count = 0;
        for (Order order : orderDAO.values()) {
            LocalDateTime orderTime = order.getOrderDatetime();
            if (orderTime != null && 
                (orderTime.isEqual(start) || orderTime.isAfter(start)) &&
                (orderTime.isEqual(end) || orderTime.isBefore(end))) {
                count++;
            }
        }
        return count;
    }

    @Override
    public BigDecimal getTotalRevenue(LocalDateTime start, LocalDateTime end) throws AnalyticsException {
        BigDecimal total = BigDecimal.ZERO;
        
        for (Order order : orderDAO.values()) {
            LocalDateTime orderTime = order.getOrderDatetime();
            // Only count paid orders
            if (orderTime != null && 
                "PAID".equals(order.getPaymentStatus()) &&
                (orderTime.isEqual(start) || orderTime.isAfter(start)) &&
                (orderTime.isEqual(end) || orderTime.isBefore(end))) {
                total = total.add(order.getTotalPrice());
            }
        }
        return total;
    }

    @Override
    public double getAverageServiceTime(LocalDateTime start, LocalDateTime end) throws AnalyticsException {
        List<Long> serviceTimes = new ArrayList<>();
        
        for (Order order : orderDAO.values()) {
            LocalDateTime orderTime = order.getOrderDatetime();
            LocalDateTime completionTime = order.getActualCompletionTime();
            
            if (orderTime != null && completionTime != null &&
                (orderTime.isEqual(start) || orderTime.isAfter(start)) &&
                (orderTime.isEqual(end) || orderTime.isBefore(end))) {
                
                long minutes = Duration.between(orderTime, completionTime).toMinutes();
                serviceTimes.add(minutes);
            }
        }
        
        if (serviceTimes.isEmpty()) {
            return 0.0;
        }
        
        double sum = serviceTimes.stream().mapToLong(Long::longValue).sum();
        return sum / serviceTimes.size();
    }

    @Override
    public Map<String, Integer> getBestSellingItems(int topN, LocalDateTime start, LocalDateTime end)
            throws AnalyticsException {
        
        Map<Integer, Integer> itemCounts = new HashMap<>();

        for (Order order : orderDAO.values()) {
            LocalDateTime orderTime = order.getOrderDatetime();
            if (orderTime != null &&
                (orderTime.isEqual(start) || orderTime.isAfter(start)) &&
                (orderTime.isEqual(end) || orderTime.isBefore(end))) {
                
                // Count items in this order
                for (int orderItemId : order.getOrderItemIds()) {
                    OrderItem item = orderItemDAO.get(String.valueOf(orderItemId));
                    if (item != null) {
                        int menuItemId = item.getMenuItemId();
                        itemCounts.merge(menuItemId, item.getQuantity(), Integer::sum);
                    }
                }
            }
        }

        Map<String, Integer> result = new LinkedHashMap<>();
        itemCounts.entrySet().stream()
            .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
            .limit(topN)
            .forEach(entry -> {
                MenuItem item = menuItemDAO.get(String.valueOf(entry.getKey()));
                if (item != null) {
                    result.put(item.getName(), entry.getValue());
                }
            });
        
        return result;
    }

    @Override
    public Map<String, Integer> getWorkstationDemand(LocalDateTime start, LocalDateTime end)
            throws AnalyticsException {

        Map<Integer, Integer> workstationCounts = new HashMap<>();

        for (KitchenTask task : kitchenTaskDAO.values()) {
            LocalDateTime taskTime = task.getScheduledStartTime();
            if (taskTime != null &&
                    (taskTime.isEqual(start) || taskTime.isAfter(start)) &&
                    (taskTime.isEqual(end) || taskTime.isBefore(end))) {

                workstationCounts.merge(task.getWorkstationId(), 1, Integer::sum);
            }
        }

        Map<String, Integer> result = new LinkedHashMap<>();
        workstationCounts.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .forEach(entry -> {
                    String name = getWorkstationDisplayName(entry.getKey());
                    result.put(name, entry.getValue());
                    // ---------------------
                });

        return result;
    }

    @Override
    public Map<String, Double> getLowStockIngredients() throws AnalyticsException {
        Map<String, Double> lowStock = new LinkedHashMap<>();
        
        for (InventoryItem item : inventoryDAO.values()) {
            if (item.needsReorder()) {
                Ingredient ingredient = ingredientDAO.get(String.valueOf(item.getIngredientId()));
                if (ingredient != null) {
                    // Calculate percentage of threshold
                    double percentage = item.getQuantityAvailable()
                        .divide(item.getReorderThreshold(), 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
                    
                    String key = ingredient.getName() + " (Restaurant " + item.getRestaurantId() + ")";
                    lowStock.put(key, percentage);
                }
            }
        }

        return lowStock.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
    }

    public Map<String, Object> getRestaurantAnalytics(int restaurantId, LocalDateTime start, LocalDateTime end) 
            throws AnalyticsException {
        
        Map<String, Object> analytics = new HashMap<>();
        
        int orderCount = 0;
        BigDecimal revenue = BigDecimal.ZERO;
        List<Long> serviceTimes = new ArrayList<>();
        
        for (Order order : orderDAO.values()) {
            if (order.getRestaurantId() != restaurantId) continue;
            
            LocalDateTime orderTime = order.getOrderDatetime();
            if (orderTime != null &&
                (orderTime.isEqual(start) || orderTime.isAfter(start)) &&
                (orderTime.isEqual(end) || orderTime.isBefore(end))) {
                
                orderCount++;
                
                if ("PAID".equals(order.getPaymentStatus())) {
                    revenue = revenue.add(order.getTotalPrice());
                }
                
                if (order.getActualCompletionTime() != null) {
                    long minutes = Duration.between(orderTime, order.getActualCompletionTime()).toMinutes();
                    serviceTimes.add(minutes);
                }
            }
        }
        
        double avgServiceTime = serviceTimes.isEmpty() ? 0.0 : 
            serviceTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        
        analytics.put("orderCount", orderCount);
        analytics.put("revenue", revenue);
        analytics.put("averageServiceTime", avgServiceTime);
        
        return analytics;
    }

    private String getWorkstationDisplayName(int workstationId) {
        Workstation ws = workstationDAO.get(String.valueOf(workstationId));
        if (ws == null) return "WS-" + workstationId + " (Unknown)";

        WorkstationType type = workstationTypeDAO.get(String.valueOf(ws.getWorkstationTypeId()));

        if (type == null) {
            return "WS-" + workstationId + " (Unknown Type)";
        }
        String typeName = switch (type.getName()) {
            case "GRILL" -> "Grill Station";
            case "FRYER" -> "Fryer Station";
            case "EGG_STATION" -> "Egg Station";
            case "VEGETABLE_PREP" -> "Vegetable Prep";
            case "ASSEMBLY" -> "Assembly & Packaging";
            default -> type.getName();
        };
        return "WS-" + workstationId + " (" + typeName + ")";
    }
}
