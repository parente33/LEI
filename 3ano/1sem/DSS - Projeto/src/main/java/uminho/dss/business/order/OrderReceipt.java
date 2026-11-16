package uminho.dss.business.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderReceipt {

    private final String orderNumber;
    private final BigDecimal totalPrice;
    private final LocalDateTime estimatedCompletion;
    private final String orderType;

    public OrderReceipt(String orderNumber, BigDecimal totalPrice,
           LocalDateTime estimatedCompletion, String orderType) {
        this.orderNumber = orderNumber;
        this.totalPrice = totalPrice;
        this.estimatedCompletion = estimatedCompletion;
        this.orderType = orderType;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public LocalDateTime getEstimatedCompletion() {
        return estimatedCompletion;
    }

    public String getOrderType() {
        return orderType;
    }

    @Override
    public String toString() {
        return String.format("OrderReceipt(#%s, €%.2f, ETA: %s, %s)",
               orderNumber, totalPrice, estimatedCompletion, orderType);
    }
}
