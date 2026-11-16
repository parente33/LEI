package uminho.dss.data.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import uminho.dss.business.kitchen.InventoryItem;
import uminho.dss.data.AbstractDAO;

public class InventoryItemDAO extends AbstractDAO<InventoryItem> {

    private static InventoryItemDAO instance = null;

    private InventoryItemDAO() {
        super("inventory_items", "restaurant_id");
    }

    public static InventoryItemDAO getInstance() {
        if (InventoryItemDAO.instance == null)
            InventoryItemDAO.instance = new InventoryItemDAO();

        return InventoryItemDAO.instance;
    }

    public InventoryItem getByCompositeKey(int restaurantId, int ingredientId) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "SELECT * FROM inventory_items WHERE restaurant_id = ? AND ingredient_id = ?")) {
            statement.setInt(1, restaurantId);
            statement.setInt(2, ingredientId);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return decodeTuple(result);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        boolean res = false;
        if (value instanceof InventoryItem && value != null) {
            InventoryItem item = (InventoryItem) value;
            InventoryItem found = getByCompositeKey(item.getRestaurantId(), item.getIngredientId());
            res = item.equals(found);
        }
        return res;
    }

    @Override
    public InventoryItem put(String key, InventoryItem value) {
        try {
            try (PreparedStatement statement = this.connection.prepareStatement(
                    "DELETE FROM inventory_items WHERE restaurant_id = ? AND ingredient_id = ?")) {
                statement.setInt(1, value.getRestaurantId());
                statement.setInt(2, value.getIngredientId());
                statement.executeUpdate();
            }

            try (PreparedStatement statement = this.connection.prepareStatement(
                    "INSERT INTO inventory_items(restaurant_id, ingredient_id, quantity_available, reorder_threshold, warehouse_quantity, last_updated) " +
                    "VALUES (?, ?, ?, ?, ?, ?)")
            ) {
                statement.setInt(1, value.getRestaurantId());
                statement.setInt(2, value.getIngredientId());
                statement.setBigDecimal(3, value.getQuantityAvailable());
                statement.setBigDecimal(4, value.getReorderThreshold());
                statement.setBigDecimal(5, value.getWarehouseQuantity());
                statement.setTimestamp(6, Timestamp.valueOf(value.getLastUpdated()));
                statement.executeUpdate();
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateQuantity(int restaurantId, int ingredientId, BigDecimal newQuantity) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "UPDATE inventory_items SET quantity_available = ?, last_updated = ? WHERE restaurant_id = ? AND ingredient_id = ?")) {
            statement.setBigDecimal(1, newQuantity);
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            statement.setInt(3, restaurantId);
            statement.setInt(4, ingredientId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void transferFromWarehouse(int restaurantId, int ingredientId, BigDecimal quantity) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "UPDATE inventory_items SET quantity_available = quantity_available + ?, warehouse_quantity = warehouse_quantity - ?, last_updated = ? WHERE restaurant_id = ? AND ingredient_id = ?")) {
            statement.setBigDecimal(1, quantity);
            statement.setBigDecimal(2, quantity);
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            statement.setInt(4, restaurantId);
            statement.setInt(5, ingredientId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected InventoryItem decodeTuple(ResultSet result) throws SQLException {
        int restaurantId = result.getInt("restaurant_id");
        int ingredientId = result.getInt("ingredient_id");
        int syntheticId = restaurantId * 10000 + ingredientId; // Create synthetic ID
        
        BigDecimal quantityAvailable = result.getBigDecimal("quantity_available");
        BigDecimal reorderThreshold = result.getBigDecimal("reorder_threshold");
        BigDecimal warehouseQuantity = result.getBigDecimal("warehouse_quantity");
        LocalDateTime lastUpdated = result.getTimestamp("last_updated").toLocalDateTime();

        return new InventoryItem(syntheticId, restaurantId, ingredientId, quantityAvailable,
                reorderThreshold, warehouseQuantity, lastUpdated);
    }
}
