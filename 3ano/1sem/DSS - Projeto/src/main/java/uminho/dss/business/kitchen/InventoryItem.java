package uminho.dss.business.kitchen;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InventoryItem {

    private int inventoryItemId;
    private int restaurantId;
    private int ingredientId;
    private BigDecimal quantityAvailable;
    private BigDecimal reorderThreshold;
    private BigDecimal warehouseQuantity;
    private LocalDateTime lastUpdated;

    public InventoryItem(int inventoryItemId, int restaurantId, int ingredientId,
           BigDecimal quantityAvailable, BigDecimal reorderThreshold,
           BigDecimal warehouseQuantity, LocalDateTime lastUpdated) {
        this.inventoryItemId = inventoryItemId;
        this.restaurantId = restaurantId;
        this.ingredientId = ingredientId;
        this.quantityAvailable = quantityAvailable;
        this.reorderThreshold = reorderThreshold;
        this.warehouseQuantity = warehouseQuantity;
        this.lastUpdated = lastUpdated;
    }

    public InventoryItem(InventoryItem other) {
        this.inventoryItemId = other.inventoryItemId;
        this.restaurantId = other.restaurantId;
        this.ingredientId = other.ingredientId;
        this.quantityAvailable = other.quantityAvailable;
        this.reorderThreshold = other.reorderThreshold;
        this.warehouseQuantity = other.warehouseQuantity;
        this.lastUpdated = other.lastUpdated;
    }

    public int getInventoryItemId() {
        return this.inventoryItemId;
    }

    public int getRestaurantId() {
        return this.restaurantId;
    }

    public int getIngredientId() {
        return this.ingredientId;
    }

    public BigDecimal getQuantityAvailable() {
        return this.quantityAvailable;
    }

    public BigDecimal getReorderThreshold() {
        return this.reorderThreshold;
    }

    public BigDecimal getWarehouseQuantity() {
        return this.warehouseQuantity;
    }

    public LocalDateTime getLastUpdated() {
        return this.lastUpdated;
    }

    public void setQuantityAvailable(BigDecimal quantity) {
        this.quantityAvailable = quantity;
    }

    public void setWarehouseQuantity(BigDecimal quantity) {
        this.warehouseQuantity = quantity;
    }

    public boolean needsReorder() {
        return this.quantityAvailable.compareTo(this.reorderThreshold) < 0;
    }

    @Override
    public Object clone() {
        return new InventoryItem(this);
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(this.inventoryItemId);
        result = 31 * result + Integer.hashCode(this.restaurantId);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        InventoryItem other = (InventoryItem) o;
        return this.inventoryItemId == other.inventoryItemId &&
               this.restaurantId == other.restaurantId;
    }

    @Override
    public String toString() {
        return String.format("InventoryItem(id=%d, restaurantId=%d, ingredientId=%d, available=%s)",
               this.inventoryItemId, this.restaurantId, this.ingredientId, this.quantityAvailable);
    }
}
