package uminho.sd.server.notification;

/**
 * Represents a callback to be executed when a notification condition is met.
 *
 * @param <T> the type of result produced by the notification condition
 */
public interface NotificationCallback<T> {

    void onResult(T result);
}
