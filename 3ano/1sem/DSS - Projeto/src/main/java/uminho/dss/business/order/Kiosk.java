package uminho.dss.business.order;

public class Kiosk {

    private int kioskId;
    private int restaurantId;
    private String status;

    public Kiosk(int kioskId, int restaurantId, String status) {
        this.kioskId = kioskId;
        this.restaurantId = restaurantId;
        this.status = status;
    }

    public Kiosk(Kiosk other) {
        this.kioskId = other.getKioskId();
        this.restaurantId = other.getRestaurantId();
        this.status = other.getStatus();
    }

    public int getKioskId() {
        return this.kioskId;
    }

    public int getRestaurantId() {
        return this.restaurantId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public Object clone() {
        return new Kiosk(this);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.kioskId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        Kiosk other = (Kiosk) o;
        return this.kioskId == other.kioskId;
    }

    @Override
    public String toString() {
        return String.format("Kiosk(id=%d, restaurantId=%d, status=%s)",
                this.kioskId, this.restaurantId, this.status);
    }
}
