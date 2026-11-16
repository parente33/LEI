package uminho.dss.ui.controller;
import uminho.dss.ui.view.*;
import uminho.dss.ui.UIException;

import uminho.dss.business.BusinessException;
import uminho.dss.business.IBusiness;
import uminho.dss.business.order.Order;

import java.util.List;

public class DeliveryController {

    private IBusiness model;
    private DeliveryView view;
    private String deliveryType;
    private int restaurantId;

    public DeliveryController(IBusiness model, String deliveryType, int restaurantId) {
        this.model = model;
        this.view = new DeliveryView(deliveryType);
        this.deliveryType = deliveryType;
        this.restaurantId = restaurantId;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void handleViewReadyOrders() throws UIException {
        try {
            List<Order> activeOrders = model.getActiveOrders(restaurantId);
            List<Order> readyOrders = activeOrders.stream()
                    .filter(o -> "READY".equals(o.getStatus()))
                    .filter(o -> o.getOrderType().equals(deliveryType))
                    .toList();

            view.displayReadyOrders(readyOrders);
        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }

    public void handleDeliverOrder() throws UIException {
        try {
            List<Order> activeOrders = model.getActiveOrders(restaurantId);
            List<Order> readyOrders = activeOrders.stream()
                    .filter(o -> "READY".equals(o.getStatus()))
                    .filter(o -> o.getOrderType().equals(deliveryType))
                    .toList();

            String orderNumber = view.promptOrderSelection(readyOrders);

            if (orderNumber == null)
                return;

            DeliveryView.DeliveryAction action = view.promptDeliveryAction();

            switch (action) {
                case COMPLETE -> {
                    model.updateOrderStatus(orderNumber, "COMPLETED");
                    view.displayDeliveryCompleted(orderNumber);
                }
                case NO_SHOW -> {
                    model.addOrderNote(orderNumber, "CLIENTE NAO COMPARECEU");
                    model.updateOrderStatus(orderNumber, "CANCELLED");
                    view.displayNoShow(orderNumber);
                }
                case CANCEL -> {
                }
            }

        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }

    public void handleViewMessages() throws UIException {
        try {
            var messages = model.getRecentMessages(10);
            view.displayMessages(messages);
        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }
}
