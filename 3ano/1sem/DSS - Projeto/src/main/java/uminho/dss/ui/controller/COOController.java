package uminho.dss.ui.controller;
import uminho.dss.ui.view.*;
import uminho.dss.ui.UIException;

import uminho.dss.business.BusinessException;
import uminho.dss.business.IBusiness;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class COOController {

    private IBusiness model;
    private COOView view;
    private String cooName;

    public COOController(IBusiness model, String cooName) {
        this.model = model;
        this.view = new COOView();
        this.cooName = cooName;
    }

    public String getCOOName() {
        return cooName;
    }

    public void handleViewAllRestaurants() throws UIException {
        try {
            LocalDateTime end = LocalDateTime.now();
            LocalDateTime start = end.minusDays(7);

            Map<Integer, COOView.RestaurantStats> statsMap = new HashMap<>();

            for (int restaurantId : new int[] { 1, 2, 3 }) {
                Map<String, Object> analytics = model.getRestaurantAnalytics(restaurantId, start, end);

                int orderCount = (int) analytics.get("orderCount");
                BigDecimal revenue = (BigDecimal) analytics.getOrDefault("revenue", BigDecimal.ZERO);
                double avgTime = (double) analytics.getOrDefault("averageServiceTime", 0.0);

                statsMap.put(restaurantId, new COOView.RestaurantStats(orderCount, revenue, avgTime));
            }

            view.displayAllRestaurantsAnalytics(statsMap);
        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }

    public void handleViewSingleRestaurant() throws UIException {
        try {
            Integer restaurantId = view.promptRestaurantSelection();
            if (restaurantId == 0)
                return;

            LocalDateTime end = LocalDateTime.now();
            LocalDateTime start = end.minusDays(7);

            Map<String, Object> analytics = model.getRestaurantAnalytics(restaurantId, start, end);

            int orderCount = (int) analytics.get("orderCount");
            BigDecimal revenue = (BigDecimal) analytics.getOrDefault("revenue", BigDecimal.ZERO);
            double avgTime = (double) analytics.getOrDefault("averageServiceTime", 0.0);

            Map<Integer, COOView.RestaurantStats> statsMap = new HashMap<>();
            statsMap.put(restaurantId, new COOView.RestaurantStats(orderCount, revenue, avgTime));
            view.displayAllRestaurantsAnalytics(statsMap);
        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }

    public void handleViewGlobalRevenue() throws UIException {
        try {
            LocalDateTime end = LocalDateTime.now();
            LocalDateTime start = end.minusDays(30);

            BigDecimal totalRevenue = model.getTotalRevenue(start, end);
            int totalOrders = model.getTotalOrderCount(start, end);

            view.displayGlobalRevenue(totalRevenue, totalOrders);
        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }

    public void handleViewWorkstationDemand() throws UIException {
        try {
            LocalDateTime end = LocalDateTime.now();
            LocalDateTime start = end.minusDays(7);

            var demand = model.getWorkstationDemand(start, end);
            view.displayWorkstationDemand(demand);
        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }

    public void handleViewGlobalLowStock() throws UIException {
        try {
            var lowStock = model.getLowStockIngredients();

            Map<String, Double> displayMap = new HashMap<>();
            for (var entry : lowStock.entrySet()) {
                displayMap.put(entry.getKey(), entry.getValue());
            }

            view.displayGlobalLowStock(displayMap);
        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }

    public void handleSendMessage() throws UIException {
        try {
            COOView.BroadcastData data = view.promptBroadcastMessage();
            if (data == null)
                return;

            if (data.isGlobal()) {
                model.sendBroadcastMessage(cooName, data.message);
                view.displayMessageSent(true, null);
            } else {
                model.sendMessageToRestaurant(cooName, data.restaurantId, data.message);
                view.displayMessageSent(false, data.restaurantId);
            }
        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }
}
