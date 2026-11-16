package uminho.dss.data.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;

import uminho.dss.business.order.Order;
import uminho.dss.data.AbstractDAO;

public class OrderDAO extends AbstractDAO<Order> {

    private static OrderDAO instance = null;

    private OrderDAO() {
        super("orders", "order_id");
    }

    public static OrderDAO getInstance() {
        if (OrderDAO.instance == null)
            OrderDAO.instance = new OrderDAO();

        return OrderDAO.instance;
    }

    @Override
    public boolean containsValue(Object value) {
        boolean res = false;
        if (value instanceof Order && value != null) {
            Order order = this.get(String.valueOf(((Order) value).getOrderId()));
            res = value.equals(order);
        }
        return res;
    }

    @Override
    public Order put(String key, Order value) {
        boolean turnAutoCommitBackOn = false;
        try {
            turnAutoCommitBackOn = this.connection.getAutoCommit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            this.connection.setAutoCommit(false);

            Order existing = null;
            boolean exists = this.containsKey(key);

            if (exists) {
                existing = this.get(key);

                try (PreparedStatement statement = this.connection.prepareStatement(
                        "UPDATE orders SET order_number=?, kiosk_id=?, restaurant_id=?, counter_id=?, order_datetime=?, order_type=?, status=?, total_price=?, payment_method=?, payment_status=?, estimated_completion_time=?, actual_completion_time=?, order_notes=? WHERE order_id=?")) {

                    statement.setString(1, value.getOrderNumber());
                    statement.setInt(2, value.getKioskId());
                    statement.setInt(3, value.getRestaurantId());
                    if (value.getCounterId() != null) {
                        statement.setInt(4, value.getCounterId());
                    } else {
                        statement.setNull(4, Types.INTEGER);
                    }
                    statement.setTimestamp(5, Timestamp.valueOf(value.getOrderDatetime()));
                    statement.setString(6, value.getOrderType());
                    statement.setString(7, value.getStatus());
                    statement.setBigDecimal(8, value.getTotalPrice());
                    statement.setString(9, value.getPaymentMethod());
                    statement.setString(10, value.getPaymentStatus());
                    statement.setTimestamp(11,
                            value.getEstimatedCompletionTime() != null
                                    ? Timestamp.valueOf(value.getEstimatedCompletionTime())
                                    : null);
                    statement.setTimestamp(12,
                            value.getActualCompletionTime() != null ? Timestamp.valueOf(value.getActualCompletionTime())
                                    : null);
                    statement.setString(13, value.getNotes());
                    statement.setInt(14, Integer.parseInt(key));

                    statement.executeUpdate();
                }
            } else {
                try (PreparedStatement statement = this.connection.prepareStatement(
                        "INSERT INTO orders(order_number, kiosk_id, restaurant_id, counter_id, order_datetime, order_type, status, total_price, payment_method, payment_status, estimated_completion_time, actual_completion_time, order_notes) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

                    statement.setString(1, value.getOrderNumber());
                    statement.setInt(2, value.getKioskId());
                    statement.setInt(3, value.getRestaurantId());
                    if (value.getCounterId() != null) {
                        statement.setInt(4, value.getCounterId());
                    } else {
                        statement.setNull(4, Types.INTEGER);
                    }
                    statement.setTimestamp(5, Timestamp.valueOf(value.getOrderDatetime()));
                    statement.setString(6, value.getOrderType());
                    statement.setString(7, value.getStatus());
                    statement.setBigDecimal(8, value.getTotalPrice());
                    statement.setString(9, value.getPaymentMethod());
                    statement.setString(10, value.getPaymentStatus());
                    statement.setTimestamp(11,
                            value.getEstimatedCompletionTime() != null
                                    ? Timestamp.valueOf(value.getEstimatedCompletionTime())
                                    : null);
                    statement.setTimestamp(12,
                            value.getActualCompletionTime() != null ? Timestamp.valueOf(value.getActualCompletionTime())
                                    : null);
                    statement.setString(13, value.getNotes());

                    statement.executeUpdate();
                }
            }

            this.connection.commit();
            return existing;
        } catch (SQLException e) {
            try {
                this.connection.rollback();
            } catch (SQLException ex) {
            }
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
    protected Order decodeTuple(ResultSet result) throws SQLException {
        int orderId = result.getInt("order_id");
        String orderNumber = result.getString("order_number");
        int kioskId = result.getInt("kiosk_id");
        int restaurantId = result.getInt("restaurant_id");
        Integer counterId = result.getObject("counter_id", Integer.class);
        LocalDateTime orderDatetime = result.getTimestamp("order_datetime").toLocalDateTime();
        String orderType = result.getString("order_type");
        String status = result.getString("status");
        BigDecimal totalPrice = result.getBigDecimal("total_price");
        String paymentMethod = result.getString("payment_method");
        String paymentStatus = result.getString("payment_status");
        Timestamp estTimestamp = result.getTimestamp("estimated_completion_time");
        LocalDateTime estimatedCompletionTime = estTimestamp != null ? estTimestamp.toLocalDateTime() : null;
        Timestamp actTimestamp = result.getTimestamp("actual_completion_time");
        LocalDateTime actualCompletionTime = actTimestamp != null ? actTimestamp.toLocalDateTime() : null;

        String notes = result.getString("order_notes");

        Order order = new Order(orderId, orderNumber, kioskId, restaurantId, orderDatetime, orderType, status,
                totalPrice, paymentMethod, paymentStatus,
                estimatedCompletionTime, actualCompletionTime);

        order.setNotes(notes);
        order.setCounterId(counterId);

        try (PreparedStatement statement = this.connection.prepareStatement(
                "SELECT order_item_id FROM order_items WHERE order_id=?")) {

            statement.setInt(1, orderId);
            ResultSet itemSet = statement.executeQuery();

            while (itemSet.next()) {
                int orderItemId = itemSet.getInt("order_item_id");
                order.addOrderItem(orderItemId);
            }
        }

        return order;
    }
    
    public int getCounterNumber(int counterId) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "SELECT counter_number FROM counters WHERE counter_id = ?")) {
            statement.setInt(1, counterId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("counter_number");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
    
    public int assignCounter(int restaurantId) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "SELECT c.counter_id, c.counter_number, " +
                "COALESCE((SELECT COUNT(*) FROM orders o WHERE o.counter_id = c.counter_id AND o.status IN ('PENDING', 'IN_PROGRESS', 'READY')), 0) as active_orders " +
                "FROM counters c WHERE c.restaurant_id = ? AND c.is_active = TRUE " +
                "ORDER BY active_orders ASC, c.counter_number ASC LIMIT 1")) {
            statement.setInt(1, restaurantId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("counter_id");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }
}
