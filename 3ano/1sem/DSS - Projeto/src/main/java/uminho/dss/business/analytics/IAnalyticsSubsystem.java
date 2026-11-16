package uminho.dss.business.analytics;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public interface IAnalyticsSubsystem {

    int getTotalOrderCount(LocalDateTime start, LocalDateTime end) throws AnalyticsException;

    BigDecimal getTotalRevenue(LocalDateTime start, LocalDateTime end) throws AnalyticsException;

    double getAverageServiceTime(LocalDateTime start, LocalDateTime end) throws AnalyticsException;

    Map<String, Integer> getBestSellingItems(int topN, LocalDateTime start, LocalDateTime end)
            throws AnalyticsException;

    Map<String, Integer> getWorkstationDemand(LocalDateTime start, LocalDateTime end)
            throws AnalyticsException;

    Map<String, Double> getLowStockIngredients() throws AnalyticsException;

    Map<String, Object> getRestaurantAnalytics(int restaurantId, LocalDateTime start, LocalDateTime end) 
            throws AnalyticsException;
}
