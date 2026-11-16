package uminho.sd.server.storage;

/*
 * ProductStats for a single product to be used in the AggregationCache.
*/
public class ProductStats {

    private final String product;
    private final int    quantity;
    private final double volume;
    private final double avgPrice;
    private final double maxPrice;

    public ProductStats(
        String product, int quantity, double volume,
        double avgPrice, double maxPrice
    ) {
        this.product  = product;
        this.quantity = quantity;
        this.volume   = volume;
        this.avgPrice = avgPrice;
        this.maxPrice = maxPrice;
    }

    public String getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getVolume() {
        return volume;
    }

    public double getAvgPrice() {
        return avgPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }
}
