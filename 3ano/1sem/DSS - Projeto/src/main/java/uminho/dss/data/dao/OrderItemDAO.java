package uminho.dss.data.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import uminho.dss.business.order.OrderItem;
import uminho.dss.data.AbstractDAO;

public class OrderItemDAO extends AbstractDAO<OrderItem> {

    private static OrderItemDAO instance = null;

    private OrderItemDAO() {
        super("order_items", "order_item_id");
    }

    public static OrderItemDAO getInstance() {
        if (OrderItemDAO.instance == null)
            OrderItemDAO.instance = new OrderItemDAO();

        return OrderItemDAO.instance;
    }

    @Override
    public boolean containsValue(Object value) {
        boolean res = false;
        if (value instanceof OrderItem && value != null) {
            OrderItem orderItem = this.get(String.valueOf(((OrderItem) value).getOrderItemId()));
            res = value.equals(orderItem);
        }
        return res;
    }

    @Override
    public OrderItem put(String key, OrderItem value) {
        boolean turnAutoCommitBackOn = false;
        try {
            turnAutoCommitBackOn = this.connection.getAutoCommit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            this.connection.setAutoCommit(false);
            OrderItem res = this.remove(key);

            try (PreparedStatement statement = this.connection.prepareStatement(
                    "INSERT INTO order_items(order_id, menu_item_id, quantity, unit_price, special_instructions) VALUES (?, ?, ?, ?, ?)")) {

                statement.setInt(1, value.getOrderId());
                statement.setInt(2, value.getMenuItemId());
                statement.setInt(3, value.getQuantity());
                statement.setBigDecimal(4, value.getUnitPrice());
                statement.setString(5, value.getSpecialInstructions());
                statement.executeUpdate();
            }

            this.connection.commit();
            return res;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (turnAutoCommitBackOn)
                    this.connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected OrderItem decodeTuple(ResultSet result) throws SQLException {
        int orderItemId = result.getInt("order_item_id");
        int orderId = result.getInt("order_id");
        int menuItemId = result.getInt("menu_item_id");
        int quantity = result.getInt("quantity");
        BigDecimal unitPrice = result.getBigDecimal("unit_price");
        String specialInstructions = result.getString("special_instructions");

        OrderItem orderItem = new OrderItem(orderItemId, orderId, menuItemId, quantity,
                unitPrice, specialInstructions);

        try (PreparedStatement statement = this.connection.prepareStatement(
                "SELECT customization_id FROM order_item_customizations WHERE order_item_id=?")) {

            statement.setInt(1, orderItemId);
            ResultSet customizationSet = statement.executeQuery();

            while (customizationSet.next()) {
                int customizationId = customizationSet.getInt("customization_id");
                orderItem.addCustomization(customizationId);
            }
        }

        return orderItem;
    }
}
