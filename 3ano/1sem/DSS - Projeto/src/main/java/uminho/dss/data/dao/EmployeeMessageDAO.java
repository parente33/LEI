package uminho.dss.data.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import uminho.dss.business.kitchen.EmployeeMessage;
import uminho.dss.data.AbstractDAO;

public class EmployeeMessageDAO extends AbstractDAO<EmployeeMessage> {

    private static EmployeeMessageDAO instance = null;

    private EmployeeMessageDAO() {
        super("employee_messages", "message_id");
    }

    public static EmployeeMessageDAO getInstance() {
        if (EmployeeMessageDAO.instance == null)
            EmployeeMessageDAO.instance = new EmployeeMessageDAO();

        return EmployeeMessageDAO.instance;
    }

    @Override
    public boolean containsValue(Object value) {
        boolean res = false;
        if (value instanceof EmployeeMessage && value != null) {
            EmployeeMessage message = this.get(String.valueOf(((EmployeeMessage) value).getMessageId()));
            res = value.equals(message);
        }
        return res;
    }

    @Override
    public EmployeeMessage put(String key, EmployeeMessage value) {
        try {
            EmployeeMessage res = this.remove(key);

            try (PreparedStatement statement = this.connection.prepareStatement(
                    "INSERT INTO employee_messages(sender_employee_id, message_text, created_at) VALUES (?, ?, ?)")) {

                statement.setInt(1, value.getSenderEmployeeId());
                statement.setString(2, value.getMessageText());
                statement.setTimestamp(3, Timestamp.valueOf(value.getCreatedAt()));
                statement.executeUpdate();
            }

            return res;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected EmployeeMessage decodeTuple(ResultSet result) throws SQLException {
        int messageId = result.getInt("message_id");
        int senderEmployeeId = result.getInt("sender_employee_id");
        String messageText = result.getString("message_text");
        LocalDateTime createdAt = result.getTimestamp("created_at").toLocalDateTime();

        return new EmployeeMessage(messageId, senderEmployeeId, messageText, createdAt);
    }
}
