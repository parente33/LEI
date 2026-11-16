package uminho.sd.server.net;

import uminho.sd.protocol.Frame;

/**
 * Represents a deferred server response.
 * Encapsulates the logic to send a response frame back to the client at a later time.
 */
public interface DeferredResponse {

    /**
     * Completes the deferred request by sending the response frame.
     *
     * @param frame The response frame to send.
     */
    void complete(Frame frame);

    /**
     * Registers an action to cancel the pending operation.
     *
     * @param cancelAction The cancellation logic.
     */
    void registerCancelAction(Runnable cancelAction);
}
