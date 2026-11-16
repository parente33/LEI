package uminho.dss.business.kitchen;

import java.time.LocalDateTime;

public class TaskInfo {

    private final int taskId;
    private final String orderNumber;
    private final String description;
    private final LocalDateTime estimatedTime;
    private final String status;
    private final int priority;

    public TaskInfo(int taskId, String orderNumber, String description,
           LocalDateTime estimatedTime, String status, int priority) {
        this.taskId = taskId;
        this.orderNumber = orderNumber;
        this.description = description;
        this.estimatedTime = estimatedTime;
        this.status = status;
        this.priority = priority;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getEstimatedTime() {
        return estimatedTime;
    }

    public String getStatus() {
        return status;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return String.format("%s | %s - ETA %s [%s]",
               orderNumber, description, estimatedTime, status);
    }
}
