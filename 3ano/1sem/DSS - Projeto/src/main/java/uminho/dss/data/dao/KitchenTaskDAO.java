package uminho.dss.data.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import uminho.dss.business.kitchen.KitchenTask;
import uminho.dss.data.AbstractDAO;

public class KitchenTaskDAO extends AbstractDAO<KitchenTask> {

    private static KitchenTaskDAO instance = null;

    private KitchenTaskDAO() {
        super("kitchen_tasks", "task_id");
    }

    public static KitchenTaskDAO getInstance() {
        if (KitchenTaskDAO.instance == null)
            KitchenTaskDAO.instance = new KitchenTaskDAO();

        return KitchenTaskDAO.instance;
    }

    @Override
    public boolean containsValue(Object value) {
        boolean res = false;
        if (value instanceof KitchenTask && value != null) {
            KitchenTask task = this.get(String.valueOf(((KitchenTask) value).getTaskId()));
            if (task != null) {
                res = task.getTaskId() == ((KitchenTask) value).getTaskId();
            }
        }
        return res;
    }

    @Override
    public KitchenTask put(String key, KitchenTask value) {
        KitchenTask previous = this.get(key);

        try {
            if (previous != null) {
                String sql = "UPDATE kitchen_tasks SET order_item_id=?, workstation_id=?, status=?, priority=?, " +
                        "estimated_duration=?, scheduled_start_time=?, actual_start_time=?, actual_completion_time=?, special_notes=? " +
                        "WHERE task_id=?";

                try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
                    statement.setInt(1, value.getOrderItemId());
                    statement.setInt(2, value.getWorkstationId());
                    statement.setString(3, value.getStatus());
                    statement.setInt(4, value.getPriority());

                    if (value.getEstimatedDuration() != null)
                        statement.setInt(5, value.getEstimatedDuration());
                    else
                        statement.setNull(5, java.sql.Types.INTEGER);

                    statement.setTimestamp(6, value.getScheduledStartTime() != null ? Timestamp.valueOf(value.getScheduledStartTime()) : null);
                    statement.setTimestamp(7, value.getActualStartTime() != null ? Timestamp.valueOf(value.getActualStartTime()) : null);
                    statement.setTimestamp(8, value.getActualCompletionTime() != null ? Timestamp.valueOf(value.getActualCompletionTime()) : null);
                    statement.setString(9, value.getSpecialNotes());

                    statement.setInt(10, Integer.parseInt(key));

                    statement.executeUpdate();
                }
            } else {
                // SE NAQ eXiSTE FAZ inSERT
                String sql = "INSERT INTO kitchen_tasks(order_item_id, workstation_id, status, priority, " +
                        "estimated_duration, scheduled_start_time, actual_start_time, actual_completion_time, special_notes) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
                    statement.setInt(1, value.getOrderItemId());
                    statement.setInt(2, value.getWorkstationId());
                    statement.setString(3, value.getStatus());
                    statement.setInt(4, value.getPriority());

                    if (value.getEstimatedDuration() != null)
                        statement.setInt(5, value.getEstimatedDuration());
                    else
                        statement.setNull(5, java.sql.Types.INTEGER);

                    statement.setTimestamp(6, value.getScheduledStartTime() != null ? Timestamp.valueOf(value.getScheduledStartTime()) : null);
                    statement.setTimestamp(7, value.getActualStartTime() != null ? Timestamp.valueOf(value.getActualStartTime()) : null);
                    statement.setTimestamp(8, value.getActualCompletionTime() != null ? Timestamp.valueOf(value.getActualCompletionTime()) : null);
                    statement.setString(9, value.getSpecialNotes());

                    statement.executeUpdate();
                }
            }
            return previous;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected KitchenTask decodeTuple(ResultSet result) throws SQLException {
        int taskId = result.getInt("task_id");
        int orderItemId = result.getInt("order_item_id");
        int workstationId = result.getInt("workstation_id");
        String status = result.getString("status");
        int priority = result.getInt("priority");
        Integer estimatedDuration = result.getObject("estimated_duration", Integer.class);

        Timestamp schedTimestamp = result.getTimestamp("scheduled_start_time");
        LocalDateTime scheduledStartTime = schedTimestamp != null ? schedTimestamp.toLocalDateTime() : null;
        Timestamp actualStartTimestamp = result.getTimestamp("actual_start_time");
        LocalDateTime actualStartTime = actualStartTimestamp != null ? actualStartTimestamp.toLocalDateTime() : null;
        Timestamp actualCompTimestamp = result.getTimestamp("actual_completion_time");
        LocalDateTime actualCompletionTime = actualCompTimestamp != null ? actualCompTimestamp.toLocalDateTime() : null;

        String specialNotes = result.getString("special_notes");

        return new KitchenTask(taskId, orderItemId, workstationId, status, priority,
                estimatedDuration, scheduledStartTime, actualStartTime,
                actualCompletionTime, specialNotes);
    }
}
