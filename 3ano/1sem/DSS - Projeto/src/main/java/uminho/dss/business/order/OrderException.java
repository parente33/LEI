package uminho.dss.business.order;

import uminho.dss.business.BusinessException;

public class OrderException extends BusinessException {

    public OrderException(String message) {
        super(message);
    }

    public OrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
