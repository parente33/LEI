package uminho.dss.data.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import uminho.dss.business.kitchen.InventoryTransaction;
import uminho.dss.data.AbstractDAO;

public class InventoryTransactionDAO extends AbstractDAO<InventoryTransaction> {

    private static InventoryTransactionDAO instance = null;

    private InventoryTransactionDAO() {
        super("inventory_transactions", "transaction_id");
    }

    public static InventoryTransactionDAO getInstance() {
        if (instance == null) {
            instance = new InventoryTransactionDAO();
        }
        return instance;
    }

    @Override
    public boolean containsValue(Object value) {
        boolean res = false;
        if (value instanceof InventoryTransaction && value != null) {
            InventoryTransaction transaction = this.get(
                    String.valueOf(((InventoryTransaction) value).getTransactionId()));
            res = value.equals(transaction);
        }
        return res;
    }

    @Override
    public InventoryTransaction put(String key, InventoryTransaction value) {
        try {
            InventoryTransaction res = this.remove(key);

            try (PreparedStatement statement = this.connection.prepareStatement(
                    "INSERT INTO inventory_transactions(restaurant_id, ingredient_id, transaction_type, quantity, transaction_datetime, notes, employee_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)")
            ) {
                statement.setInt(1, value.getRestaurantId());
                statement.setInt(2, value.getIngredientId());
                statement.setString(3, value.getTransactionType());
                statement.setBigDecimal(4, value.getQuantity());
                statement.setTimestamp(5, Timestamp.valueOf(value.getTransactionDatetime()));
                statement.setString(6, value.getNotes());
                if (value.getEmployeeId() != null) {
                    statement.setInt(7, value.getEmployeeId());
                } else {
                    statement.setNull(7, java.sql.Types.INTEGER);
                }
                statement.executeUpdate();
            }

            return res;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected InventoryTransaction decodeTuple(ResultSet result) throws SQLException {
        int transactionId = result.getInt("transaction_id");
        int restaurantId = result.getInt("restaurant_id");
        int ingredientId = result.getInt("ingredient_id");
        String transactionType = result.getString("transaction_type");
        BigDecimal quantity = result.getBigDecimal("quantity");
        LocalDateTime transactionDatetime = result.getTimestamp("transaction_datetime").toLocalDateTime();
        String notes = result.getString("notes");
        int empId = result.getInt("employee_id");
        Integer employeeId = result.wasNull() ? null : empId;

        return new InventoryTransaction(transactionId, restaurantId, ingredientId, transactionType,
                quantity, transactionDatetime, notes, employeeId);
    }
}
