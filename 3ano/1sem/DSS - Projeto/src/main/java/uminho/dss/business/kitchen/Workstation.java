package uminho.dss.business.kitchen;

public class Workstation {

    private int workstationId;
    private int restaurantId;
    private int workstationTypeId; // Novo campo
    private boolean isActive;

    public Workstation(int workstationId, int restaurantId, int workstationTypeId, boolean isActive) {
        this.workstationId = workstationId;
        this.restaurantId = restaurantId;
        this.workstationTypeId = workstationTypeId;
        this.isActive = isActive;
    }

    // Construtor de cópia
    public Workstation(Workstation other) {
        this.workstationId = other.workstationId;
        this.restaurantId = other.restaurantId;
        this.workstationTypeId = other.workstationTypeId;
        this.isActive = other.isActive;
    }

    public int getWorkstationId() {
        return this.workstationId;
    }

    public int getRestaurantId() {
        return this.restaurantId;
    }

    public int getWorkstationTypeId() {
        return this.workstationTypeId;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    @Override
    public Object clone() {
        return new Workstation(this);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.workstationId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workstation that = (Workstation) o;
        return workstationId == that.workstationId;
    }

    @Override
    public String toString() {
        return String.format("Workstation(id=%d, type=%d, restaurant=%d)",
                workstationId, workstationTypeId, restaurantId);
    }
}
