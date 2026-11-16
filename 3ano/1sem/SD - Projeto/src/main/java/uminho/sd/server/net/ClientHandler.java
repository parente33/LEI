package uminho.sd.server.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import uminho.sd.protocol.Frame;
import uminho.sd.protocol.TaggedConnection;
import uminho.sd.protocol.Serialization;
import uminho.sd.server.auth.AuthService;
import uminho.sd.server.core.WorkerPool;
import uminho.sd.server.storage.Repository;

/**
 * Handles a single client connection.
 */
public class ClientHandler implements Runnable {

    private final TaggedConnection connection;

    private final AuthService authService;
    private final Repository  repository;

    private RequestDispatcher dispatcher;
    private String authenticatedUser;

    private final ReentrantLock lock;
    private final WorkerPool workerPool;

    /* Pending cancellation tokens for cleanup */
    private final List<Runnable> pendingCancellations;

    public ClientHandler(
            TaggedConnection connection, AuthService authService,
            Repository repository, WorkerPool workerPool) {
        this.connection = connection;

        this.authService = authService;
        this.repository  = repository;

        this.workerPool = workerPool;

        this.authenticatedUser = null;
        this.lock = new ReentrantLock();
        this.pendingCancellations = new ArrayList<>();
    }

    /**
     * Main loop of the client handler. Continuously reads requests from the client
     * connection, submits them to the worker pool for processing, and handles
     * disconnections or network errors.
     */
    @Override
    public void run() {
        try {
            dispatcher = new RequestDispatcher(authService, repository);

            while (!Thread.currentThread().isInterrupted()) {
                /* Read request frame (blocking, sequential) */
                Frame request = connection.receive();

                try {
                    workerPool.submit(() -> processRequest(request));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

        } catch (IOException e) {
            /* Client disconnected or network error */

            System.out.println(
                "Client disconnected: " +
                connection.getSocket().getRemoteSocketAddress()
            );

        } finally {
            cleanup();
        }
    }

    private void processRequest(Frame request) {
        try {
            /* Get current auth state properly protected */
            String user = null;
            lock.lock();
            try {
                user = authenticatedUser;
            } finally {
                lock.unlock();
            }

            /* Enforce login ordering: deny any non-auth request until authenticatedUser set */
            if (!isAuthOrRegister(request) && user == null) {
                Frame response = errorResponse(request.getTag(), "Not authenticated");
                connection.send(response);
                return;
            }

            Frame response = dispatcher.dispatch(request, user, new DeferredResponse() {
                @Override
                public void complete(Frame frame) {
                    try {
                        connection.send(frame);
                    } catch (IOException e) {
                        System.err.println(
                            "Failed to send deferred response: " + e.getMessage()
                        );
                    }
                }

                @Override
                public void registerCancelAction(Runnable cancelAction) {
                    lock.lock();
                    try {
                        pendingCancellations.add(cancelAction);
                    } finally {
                        lock.unlock();
                    }
                }
            });

            /* If deferred, we are done for now */
            if (response == null) {
                return;
            }

            /* Update authentication state if login/register succeeded */
            if (response.getType() == Frame.RESPONSE_OK) {
                if (request.getType() == Frame.LOGIN || request.getType() == Frame.REGISTER) {
                    /* Extract username from request for tracking */
                    try {
                        byte[] payload = request.getPayload();
                        String[] auth = Serialization.deserializeAuth(payload);

                        lock.lock();
                        try {
                            authenticatedUser = auth[0];
                        } finally {
                            lock.unlock();
                        }
                    } catch (IOException e) {
                        /* Ignore - shouldn't happen if dispatcher succeeded */
                    }
                }
            }
            connection.send(response);

        } catch (IOException e) {
            String msg = e.getMessage();
            if (msg != null && (msg.contains("Socket closed")
                            ||  msg.contains("Broken pipe")
                            ||  msg.contains("Connection reset"))
            ) {
                /* Normal disconnect during write */
            } else {
                System.err.println(
                    "Error processing/sending response: " + e.getMessage()
                );
            }
        }
    }

    private boolean isAuthOrRegister(Frame req) {
        byte t = req.getType();
        return t == Frame.LOGIN || t == Frame.REGISTER;
    }

    private Frame errorResponse(int tag, String message) {
        try {
            byte[] payload = Serialization.serializeError(message);
            return new Frame(tag, Frame.RESPONSE_ERROR, payload);
        } catch (IOException e) {
            return new Frame(tag, Frame.RESPONSE_ERROR, Serialization.serializeVoid());
        }
    }

    private void cleanup() {
        /* Run cancellation tokens */
        lock.lock();
        try {
            for (Runnable cancel : pendingCancellations) {
                try {
                    cancel.run();
                } catch (Exception e) {
                    /* Ignore */
                }
            }
            pendingCancellations.clear();
        } finally {
            lock.unlock();
        }

        try {
            if (connection != null) {
                connection.close();
            }
        } catch (IOException e) {
            /* Ignore */
        }
        authenticatedUser = null;
    }
}
