package uminho.dss.business.kitchen;


public class Display {

    private int displayId;
    private Integer workstationId;   // null se for display de recolha
    private Integer restaurantId;    // null se for display de workstation

    public Display(int displayId, Integer workstationId, Integer restaurantId) {
        if ((workstationId == null && restaurantId == null) ||
            (workstationId != null && restaurantId != null)) {
            throw new IllegalArgumentException(
                "A display must be associated either with a workstation or with a restaurant, but not both."
            );
        }

        this.displayId = displayId;
        this.workstationId = workstationId;
        this.restaurantId = restaurantId;
    }

    public Display(Display other) {
        this.displayId = other.displayId;
        this.workstationId = other.workstationId;
        this.restaurantId = other.restaurantId;
    }

    public int getDisplayId() {
        return this.displayId;
    }

    public Integer getWorkstationId() {
        return this.workstationId;
    }

    public Integer getRestaurantId() {
        return this.restaurantId;
    }

    public boolean isKitchenDisplay() {
        return this.workstationId != null;
    }

    public boolean isPickupDisplay() {
        return this.restaurantId != null;
    }

    @Override
    public Object clone() {
        return new Display(this);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.displayId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        Display other = (Display) o;
        return this.displayId == other.displayId;
    }

    @Override
    public String toString() {
        if (isKitchenDisplay()) {
            return String.format(
                "Display(id=%d, workstationId=%d)",
                this.displayId, this.workstationId
            );
        } else {
            return String.format(
                "Display(id=%d, restaurantId=%d)",
                this.displayId, this.restaurantId
            );
        }
    }
}
