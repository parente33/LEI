package uminho.dss.business.kitchen;

import java.time.LocalDateTime;

public class EmployeeMessage {

    private int messageId;
    private int senderEmployeeId;
    private String messageText;
    private LocalDateTime createdAt;

    public EmployeeMessage(int messageId, int senderEmployeeId, String messageText,
           LocalDateTime createdAt) {
        this.messageId = messageId;
        this.senderEmployeeId = senderEmployeeId;
        this.messageText = messageText;
        this.createdAt = createdAt;
    }

    public EmployeeMessage(EmployeeMessage other) {
        this.messageId = other.messageId;
        this.senderEmployeeId = other.senderEmployeeId;
        this.messageText = other.messageText;
        this.createdAt = other.createdAt;
    }

    public int getMessageId() {
        return this.messageId;
    }

    public int getSenderEmployeeId() {
        return this.senderEmployeeId;
    }

    public String getMessageText() {
        return this.messageText;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public Object clone() {
        return new EmployeeMessage(this);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.messageId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        EmployeeMessage other = (EmployeeMessage) o;
        return this.messageId == other.messageId;
    }

    @Override
    public String toString() {
        return String.format("EmployeeMessage(id=%d, from=%d, text=%s)",
               this.messageId, this.senderEmployeeId,
               this.messageText.length() > 50 ? this.messageText.substring(0, 50) + "..." : this.messageText);
    }
}
