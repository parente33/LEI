package uminho.sd.protocol;

/**
 * Immutable data class for events.
 */
public class SalesEvent {

    private final String product;
    private final int    quantity;
    private final double price;

    /**
     * Creates a new immutable SalesEvent.
     *
     * @param product  Product name (not null)
     * @param quantity Quantity sold
     * @param price    Price per unit
     */
    public SalesEvent(String product, int quantity, double price) {
        this.product  = product;
        this.quantity = quantity;
        this.price    = price;
    }

    /**
     * Returns the product name for this sales event.
     *
     * @return the product as a string
     */
    public String getProduct() {
        return product;
    }

    /**
     * Returns the quantity sold in this sales event.
     *
     * @return the quantity as an integer
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Returns the price of the product in this sales event.
     *
     * @return the price as a double
     */
    public double getPrice() {
        return price;
    }

    /**
     * Returns a string representation of the sales event.
     *
     * @return a string in the format
     *         {@code SalesEvent{product='name', qty=quantity, price=price}}
     */
    @Override
    public String toString() {
        return
            "SalesEvent{product='" + product +
            "', qty=" + quantity + ", price=" + price + "}";
    }
}
