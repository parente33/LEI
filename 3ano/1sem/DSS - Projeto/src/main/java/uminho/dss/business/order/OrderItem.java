package uminho.dss.business.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderItem {

    private int orderItemId;
    private int orderId;
    private int menuItemId;
    private int quantity;
    private BigDecimal unitPrice;
    private String specialInstructions;
    private List<Integer> customizationIds;

    public OrderItem(int orderItemId, int orderId, int menuItemId, int quantity,
           BigDecimal unitPrice, String specialInstructions) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.specialInstructions = specialInstructions;
        this.customizationIds = new ArrayList<>();
    }

    public OrderItem(OrderItem other) {
        this.orderItemId = other.orderItemId;
        this.orderId = other.orderId;
        this.menuItemId = other.menuItemId;
        this.quantity = other.quantity;
        this.unitPrice = other.unitPrice;
        this.specialInstructions = other.specialInstructions;
        this.customizationIds = new ArrayList<>(other.customizationIds);
    }

    public int getOrderItemId() {
        return this.orderItemId;
    }

    public int getOrderId() {
        return this.orderId;
    }

    public int getMenuItemId() {
        return this.menuItemId;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }

    public String getSpecialInstructions() {
        return this.specialInstructions;
    }

    public List<Integer> getCustomizationIds() {
        return new ArrayList<>(this.customizationIds);
    }

    public void addCustomization(int customizationId) {
        this.customizationIds.add(customizationId);
    }

    @Override
    public Object clone() {
        return new OrderItem(this);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.orderItemId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        OrderItem other = (OrderItem) o;
        return this.orderItemId == other.orderItemId;
    }

    @Override
    public String toString() {
        return String.format("OrderItem(id=%d, menuItemId=%d, quantity=%d, price=%s)",
               this.orderItemId, this.menuItemId, this.quantity, this.unitPrice);
    }
}
