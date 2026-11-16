package uminho.dss.business.kitchen;

import java.time.LocalDateTime;

public class KitchenTask {

    private int taskId;
    private int orderItemId;
    private int workstationId;
    private String status;
    private int priority;
    private Integer estimatedDuration;
    private LocalDateTime scheduledStartTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualCompletionTime;
    private String specialNotes;

    public KitchenTask(int taskId, int orderItemId, int workstationId,
           String status, int priority, Integer estimatedDuration,
           LocalDateTime scheduledStartTime, LocalDateTime actualStartTime,
           LocalDateTime actualCompletionTime, String specialNotes) {
        this.taskId = taskId;
        this.orderItemId = orderItemId;
        this.workstationId = workstationId;
        this.status = status;
        this.priority = priority;
        this.estimatedDuration = estimatedDuration;
        this.scheduledStartTime = scheduledStartTime;
        this.actualStartTime = actualStartTime;
        this.actualCompletionTime = actualCompletionTime;
        this.specialNotes = specialNotes;
    }

    public KitchenTask(KitchenTask other) {
        this.taskId = other.taskId;
        this.orderItemId = other.orderItemId;
        this.workstationId = other.workstationId;
        this.status = other.status;
        this.priority = other.priority;
        this.estimatedDuration = other.estimatedDuration;
        this.scheduledStartTime = other.scheduledStartTime;
        this.actualStartTime = other.actualStartTime;
        this.actualCompletionTime = other.actualCompletionTime;
        this.specialNotes = other.specialNotes;
    }

    public int getTaskId() {
        return this.taskId;
    }

    public int getOrderItemId() {
        return this.orderItemId;
    }

    public int getWorkstationId() {
        return this.workstationId;
    }

    public String getStatus() {
        return this.status;
    }

    public int getPriority() {
        return this.priority;
    }

    public Integer getEstimatedDuration() {
        return this.estimatedDuration;
    }

    public LocalDateTime getScheduledStartTime() {
        return this.scheduledStartTime;
    }

    public LocalDateTime getActualStartTime() {
        return this.actualStartTime;
    }

    public LocalDateTime getActualCompletionTime() {
        return this.actualCompletionTime;
    }

    public String getSpecialNotes() {
        return this.specialNotes;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setScheduledStartTime(LocalDateTime time) {
        this.scheduledStartTime = time;
    }

    public void setActualStartTime(LocalDateTime time) {
        this.actualStartTime = time;
    }

    public void setActualCompletionTime(LocalDateTime time) {
        this.actualCompletionTime = time;
    }

    @Override
    public Object clone() {
        return new KitchenTask(this);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.taskId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        KitchenTask other = (KitchenTask) o;
        return this.taskId == other.taskId;
    }

    @Override
    public String toString() {
        return String.format("KitchenTask(id=%d, orderItemId=%d, workstation=%d, status=%s)",
               this.taskId, this.orderItemId, this.workstationId, this.status);
    }
}
