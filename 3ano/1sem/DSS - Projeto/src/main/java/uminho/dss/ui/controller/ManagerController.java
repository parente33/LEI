package uminho.dss.ui.controller;
import uminho.dss.ui.view.*;
import uminho.dss.ui.UIException;

import uminho.dss.business.BusinessException;
import uminho.dss.business.IBusiness;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class ManagerController {

    private IBusiness model;
    private ManagerView view;
    private String managerName;
    private int restaurantId;

    public ManagerController(IBusiness model, String managerName, int restaurantId) {
        this.model = model;
        this.view = new ManagerView();
        this.managerName = managerName;
        this.restaurantId = restaurantId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void handleViewAnalytics() throws UIException {
        try {
            LocalDateTime end = LocalDateTime.now();
            LocalDateTime start = end.minusDays(7);

            Map<String, Object> analytics = model.getRestaurantAnalytics(restaurantId, start, end);

            int orderCount = (int) analytics.get("orderCount");
            BigDecimal revenue = (BigDecimal) analytics.getOrDefault("revenue", BigDecimal.ZERO);
            double avgTime = (double) analytics.getOrDefault("averageServiceTime", 0.0);

            view.displayAnalytics(orderCount, revenue, avgTime);
        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }

    public void handleViewWorkstations() throws UIException {
        try {
            var workstations = model.getActiveWorkstations(restaurantId);
            view.displayWorkstations(workstations);
        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }

    public void handleCheckLowStock() throws UIException {
        try {
            var lowStock = model.getLowStockIngredients();
            view.displayLowStock(lowStock);
        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }

    public void handleSendMessage() throws UIException {
        try {
            String message = view.promptMessage();
            model.sendMessageToRestaurant(managerName, restaurantId, message);
            view.displayMessageSent(restaurantId);
        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }

    public void handleViewBestSellers() throws UIException {
        try {
            LocalDateTime end = LocalDateTime.now();
            LocalDateTime start = end.minusDays(30);

            var bestSellers = model.getBestSellingItems(10, start, end);
            view.displayBestSellers(bestSellers);
        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }
}
