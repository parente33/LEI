package uminho.dss.business.kitchen;

import uminho.dss.business.BusinessException;

public class KitchenException extends BusinessException {

    public KitchenException(String message) {
        super(message);
    }

    public KitchenException(String message, Throwable cause) {
        super(message, cause);
    }
}
