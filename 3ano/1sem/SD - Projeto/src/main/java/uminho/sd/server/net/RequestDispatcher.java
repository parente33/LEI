package uminho.sd.server.net;

import java.io.IOException;
import java.util.Set;

import uminho.sd.protocol.Frame;
import uminho.sd.protocol.SalesEvent;
import uminho.sd.protocol.Serialization;
import uminho.sd.server.auth.AuthService;
import uminho.sd.server.storage.Repository;

import java.util.List;

/**
 * Dispatches incoming frames to appropriate service methods.
 */
public class RequestDispatcher {

    private final AuthService authService;
    private final Repository  repository;

    public RequestDispatcher(AuthService authService, Repository repository) {
        this.authService = authService;
        this.repository  = repository;
    }

    /**
     * Dispatch frame to appropriate handler based on type
     *
     * @param request           The incoming request frame
     * @param authenticatedUser Current authenticated user (null if not authenticated)
     * @param deferred          The deferred response handler
     * @return Response frame (or null if deferred)
     */
    public Frame dispatch(Frame request, String authenticatedUser, DeferredResponse deferred) {
        try {
            return switch (request.getType()) {
                case Frame.REGISTER -> handleRegister(request);
                case Frame.LOGIN -> handleLogin(request);
                case Frame.ADD_EVENT -> handleAddEvent(request, authenticatedUser);
                case Frame.QUERY_SALES_QUANTITY -> handleSalesQuantity(request, authenticatedUser);
                case Frame.QUERY_SALES_VOLUME -> handleSalesVolume(request, authenticatedUser);
                case Frame.QUERY_AVG_PRICE -> handleAvgPrice(request, authenticatedUser);
                case Frame.QUERY_MAX_PRICE -> handleMaxPrice(request, authenticatedUser);
                case Frame.FILTER_EVENTS -> handleFilterEvents(request, authenticatedUser);
                case Frame.NOTIFY_SIMULTANEOUS -> handleSimultaneousSales(request, authenticatedUser, deferred);
                case Frame.NOTIFY_CONSECUTIVE -> handleConsecutiveSales(request, authenticatedUser, deferred);
                case Frame.START_NEW_DAY -> handleStartNewDay(request, authenticatedUser);
                default -> errorResponse(request.getTag(), "Unknown request type");
            };
        } catch (Exception e) {
            return errorResponse(request.getTag(), "Internal error: " + e.getMessage());
        }
    }

    /* ---- Auth Handlers ---- */

    private Frame handleRegister(Frame request) throws IOException {
        String[] data = Serialization.deserializeAuth(request.getPayload());

        boolean success = authService.register(data[0], data[1]);
        if (success) {
            return new Frame(request.getTag(), Frame.RESPONSE_OK,
                Serialization.serializeBoolean(true)
            );
        } else {
            return errorResponse(request.getTag(), "User already exists");
        }
    }

    private Frame handleLogin(Frame request) throws IOException {
        String[] data = Serialization.deserializeAuth(request.getPayload());

        boolean success = authService.login(data[0], data[1]);
        if (success) {
            return new Frame(
                request.getTag(), Frame.RESPONSE_OK,
                Serialization.serializeNullableString(data[0])
            );
        } else {
            return errorResponse(request.getTag(), "Invalid credentials");
        }
    }

    /* ---- Event Handlers ---- */

    private Frame handleAddEvent(Frame request, String user) throws IOException {
        if (!isAuthenticated(user)) {
            return errorResponse(request.getTag(), "Not authenticated");
        }

        Object[] data = Serialization.deserializeAddEvent(request.getPayload());

        String product  = (String) data[0];
        int    quantity = (int)    data[1];
        double price    = (double) data[2];

        repository.addEvent(product, quantity, price);
        return new Frame(request.getTag(), Frame.RESPONSE_OK, Serialization.serializeVoid());
    }

    /* ---- Query Handlers ---- */

    private Frame handleSalesQuantity(Frame request, String user) throws IOException {
        if (!isAuthenticated(user)) {
            return errorResponse(request.getTag(), "Not authenticated");
        }

        Object[] data = Serialization.deserializeProductDaysQuery(request.getPayload());

        String product = (String) data[0];
        int    days    = (int)    data[1];

        int qty = repository.getSalesQuantity(product, days);
        return new Frame(request.getTag(), Frame.RESPONSE_OK, Serialization.serializeInt(qty));
    }

    private Frame handleSalesVolume(Frame request, String user) throws IOException {
        if (!isAuthenticated(user)) {
            return errorResponse(request.getTag(), "Not authenticated");
        }

        Object[] data = Serialization.deserializeProductDaysQuery(request.getPayload());

        String product = (String) data[0];
        int    days    = (int)    data[1];

        double vol = repository.getSalesVolume(product, days);
        return new Frame(request.getTag(), Frame.RESPONSE_OK, Serialization.serializeDouble(vol));
    }

    private Frame handleAvgPrice(Frame request, String user) throws IOException {
        if (!isAuthenticated(user)) {
            return errorResponse(request.getTag(), "Not authenticated");
        }

        Object[] data = Serialization.deserializeProductDaysQuery(request.getPayload());

        String product = (String) data[0];
        int    days    = (int)    data[1];

        double avg = repository.getAvgPrice(product, days);
        return new Frame(request.getTag(), Frame.RESPONSE_OK, Serialization.serializeDouble(avg));
    }

    private Frame handleMaxPrice(Frame request, String user) throws IOException {
        if (!isAuthenticated(user)) {
            return errorResponse(request.getTag(), "Not authenticated");
        }

        Object[] data = Serialization.deserializeProductDaysQuery(request.getPayload());

        String product = (String) data[0];
        int    days    = (int)    data[1];

        double max = repository.getMaxPrice(product, days);
        return new Frame(request.getTag(), Frame.RESPONSE_OK, Serialization.serializeDouble(max));
    }

    private Frame handleFilterEvents(Frame request, String user) throws IOException {
        if (!isAuthenticated(user)) {
            return errorResponse(request.getTag(), "Not authenticated");
        }

        Object[] filter = Serialization.deserializeFilterEventsRequest(request.getPayload());

        @SuppressWarnings("unchecked")  /* lets just hope */
        Set<String> products = (Set<String>) filter[0];
        int         days     = (int)         filter[1];

        List<SalesEvent> events = repository.filterEvents(products, days);
        return new Frame(request.getTag(), Frame.RESPONSE_OK, Serialization.serializeEventList(events));
    }

    /* ---- Notification Handlers ---- */

    private Frame handleSimultaneousSales(
        Frame request, String user, DeferredResponse deferred
    ) throws IOException {
        if (!isAuthenticated(user)) {
            return errorResponse(request.getTag(), "Not authenticated");
        }

        String[] products = Serialization.deserializeSimultaneousSales(request.getPayload());

        String p1 = products[0];
        String p2 = products[1];

        /* Register Callback */
        Runnable cancelAction = repository.getObserver().registerSimultaneous(p1, p2, (result) -> {
            try {
                byte[] payload = Serialization.serializeBoolean(result);
                deferred.complete(new Frame(request.getTag(), Frame.RESPONSE_OK, payload));
            } catch (IOException e) {
                System.err.println("Failed to serialize deferred response for tag " + request.getTag());
            }
        });

        deferred.registerCancelAction(cancelAction);

        /* Return null to signal deferred response */
        return null;
    }

    private Frame handleConsecutiveSales(
        Frame request, String user, DeferredResponse deferred
    ) throws IOException {
        if (!isAuthenticated(user)) {
            return errorResponse(request.getTag(), "Not authenticated");
        }

        int n = Serialization.deserializeConsecutiveSales(request.getPayload());

        /* Register Callback */
        Runnable cancelAction = repository.getObserver().registerConsecutive(n, (result) -> {
            try {
                byte[] payload = Serialization.serializeNullableString(result);
                deferred.complete(new Frame(request.getTag(), Frame.RESPONSE_OK, payload));
            } catch (IOException e) {
                System.err.println(
                    "Failed to serialize deferred response for tag " + request.getTag()
                );
            }
        });

        deferred.registerCancelAction(cancelAction);

        /* Return null to signal deferred response */
        return null;
    }

    /* ---- Admin Handlers ---- */

    private Frame handleStartNewDay(Frame request, String user) throws IOException {
        if (!isAuthenticated(user)) {
            return errorResponse(request.getTag(), "Not authenticated");
        }

        repository.startNewDay();

        return new Frame(request.getTag(), Frame.RESPONSE_OK, Serialization.serializeVoid());
    }

    /* ---- Helpers ---- */

    private boolean isAuthenticated(String user) {
        return user != null;
    }

    private Frame errorResponse(int tag, String message) {
        try {
            byte[] payload = Serialization.serializeError(message);
            return new Frame(tag, Frame.RESPONSE_ERROR, payload);
        } catch (IOException e) {
            return new Frame(tag, Frame.RESPONSE_ERROR, Serialization.serializeVoid());
        }
    }
}
