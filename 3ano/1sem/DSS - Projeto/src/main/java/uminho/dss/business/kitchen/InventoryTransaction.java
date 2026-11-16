package uminho.dss.business.kitchen;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InventoryTransaction {

    private int transactionId;
    private int restaurantId;
    private int ingredientId;
    private String transactionType;
    private BigDecimal quantity;
    private LocalDateTime transactionDatetime;
    private String notes;
    private Integer employeeId;

    public InventoryTransaction(int transactionId, int restaurantId, int ingredientId,
                                String transactionType, BigDecimal quantity,
                                LocalDateTime transactionDatetime, String notes, Integer employeeId) {
        this.transactionId = transactionId;
        this.restaurantId = restaurantId;
        this.ingredientId = ingredientId;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.transactionDatetime = transactionDatetime;
        this.notes = notes;
        this.employeeId = employeeId;
    }

    public InventoryTransaction(InventoryTransaction other) {
        this.transactionId = other.transactionId;
        this.restaurantId = other.restaurantId;
        this.ingredientId = other.ingredientId;
        this.transactionType = other.transactionType;
        this.quantity = other.quantity;
        this.transactionDatetime = other.transactionDatetime;
        this.notes = other.notes;
        this.employeeId = other.employeeId;
    }


    public int getTransactionId() {
        return transactionId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public LocalDateTime getTransactionDatetime() {
        return transactionDatetime;
    }

    public String getNotes() {
        return notes;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    @Override
    public Object clone() {
        return new InventoryTransaction(this);
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(transactionId);
        result = 31 * result + Integer.hashCode(restaurantId);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        InventoryTransaction other = (InventoryTransaction) o;
        return this.transactionId == other.transactionId &&
               this.restaurantId == other.restaurantId;
    }

    @Override
    public String toString() {
        return String.format("InventoryTransaction(id=%d, restaurantId=%d, ingredientId=%d, type=%s, quantity=%s)",
                transactionId, restaurantId, ingredientId, transactionType, quantity);
    }
}
