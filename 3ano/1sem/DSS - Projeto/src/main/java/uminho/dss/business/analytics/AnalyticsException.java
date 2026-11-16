package uminho.dss.business.analytics;

import uminho.dss.business.BusinessException;

public class AnalyticsException extends BusinessException {

    public AnalyticsException(String message) {
        super(message);
    }

    public AnalyticsException(String message, Throwable cause) {
        super(message, cause);
    }
}
