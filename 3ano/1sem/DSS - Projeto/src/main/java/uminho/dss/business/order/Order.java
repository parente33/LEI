package uminho.dss.business.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private int orderId;
    private String orderNumber;
    private int kioskId;
    private int restaurantId;
    private Integer counterId;
    private LocalDateTime orderDatetime;
    private String orderType;
    private String status;
    private BigDecimal totalPrice;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime estimatedCompletionTime;
    private LocalDateTime actualCompletionTime;
    private List<Integer> orderItemIds;
    private String notes;
    private int priority;

    public Order(int orderId, String orderNumber, int kioskId, int restaurantId, LocalDateTime orderDatetime, 
            String orderType, String status, BigDecimal totalPrice, String paymentMethod, String paymentStatus,
            LocalDateTime estimatedCompletionTime, LocalDateTime actualCompletionTime) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.kioskId = kioskId;
        this.restaurantId = restaurantId;
        this.counterId = null;
        this.orderDatetime = orderDatetime;
        this.orderType = orderType;
        this.status = status;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.estimatedCompletionTime = estimatedCompletionTime;
        this.actualCompletionTime = actualCompletionTime;
        this.orderItemIds = new ArrayList<>();
        this.priority = 0;
        this.notes = "";
    }

    public Order(Order other) {
        this.orderId = other.getOrderId();
        this.orderNumber = other.getOrderNumber();
        this.kioskId = other.getKioskId();
        this.restaurantId = other.getRestaurantId();
        this.counterId = other.getCounterId();
        this.orderDatetime = other.getOrderDatetime();
        this.orderType = other.getOrderType();
        this.status = other.getStatus();
        this.totalPrice = other.getTotalPrice();
        this.paymentMethod = other.getPaymentMethod();
        this.paymentStatus = other.getPaymentStatus();
        this.estimatedCompletionTime = other.getEstimatedCompletionTime();
        this.actualCompletionTime = other.getActualCompletionTime();
        this.orderItemIds = new ArrayList<>(other.getOrderItemIds());
        this.priority = other.getPriority();
        this.notes = other.getNotes();
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getCounterId() { return counterId; }
    public void setCounterId(Integer counterId) { this.counterId = counterId; }

    public int getOrderId() {
        return this.orderId;
    }

    public String getOrderNumber() {
        return this.orderNumber;
    }

    public int getKioskId() {
        return this.kioskId;
    }

    public int getRestaurantId() {
        return this.restaurantId;
    }

    public LocalDateTime getOrderDatetime() {
        return this.orderDatetime;
    }

    public String getOrderType() {
        return this.orderType;
    }

    public String getStatus() {
        return this.status;
    }

    public BigDecimal getTotalPrice() {
        return this.totalPrice;
    }

    public String getPaymentMethod() {
        return this.paymentMethod;
    }

    public String getPaymentStatus() {
        return this.paymentStatus;
    }

    public LocalDateTime getEstimatedCompletionTime() {
        return this.estimatedCompletionTime;
    }

    public LocalDateTime getActualCompletionTime() {
        return this.actualCompletionTime;
    }

    public List<Integer> getOrderItemIds() {
        return new ArrayList<>(this.orderItemIds);
    }

    public int getPriority() {
        return this.priority;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setEstimatedCompletionTime(LocalDateTime time) {
        this.estimatedCompletionTime = time;
    }

    public void setActualCompletionTime(LocalDateTime time) {
        this.actualCompletionTime = time;
    }

    public void addOrderItem(int orderItemId) {
        this.orderItemIds.add(orderItemId);
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void incrementPriority() {
        this.priority++;
    }

    @Override
    public Object clone() {
        return new Order(this);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.orderId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        Order other = (Order) o;
        return this.orderId == other.orderId;
    }

    @Override
    public String toString() {
        return String.format("Order(id=%d, number=%s, kiosk=%d, restaurant=%d, counter=%s, type=%s, status=%s, total=%s, priority=%d)",
                this.orderId, this.orderNumber, this.kioskId, this.restaurantId, 
                this.counterId != null ? this.counterId : "N/A", 
                this.orderType, this.status, this.totalPrice, this.priority);
    }
}
