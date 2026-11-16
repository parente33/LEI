package uminho.dss.business.kitchen;

public class Employee {

    private int employeeId;
    private int restaurantId; // novo atributo
    private String name;
    private String role;
    private boolean isActive;

    // Construtor principal
    public Employee(int employeeId, int restaurantId, String name, String role, boolean isActive) {
        this.employeeId = employeeId;
        this.restaurantId = restaurantId;
        this.name = name;
        this.role = role;
        this.isActive = isActive;
    }

    // Construtor de cópia
    public Employee(Employee other) {
        this.employeeId = other.employeeId;
        this.restaurantId = other.restaurantId;
        this.name = other.name;
        this.role = other.role;
        this.isActive = other.isActive;
    }

    // Getters
    public int getEmployeeId() {
        return this.employeeId;
    }

    public int getRestaurantId() {
        return this.restaurantId;
    }

    public String getName() {
        return this.name;
    }

    public String getRole() {
        return this.role;
    }

    public boolean isActive() {
        return this.isActive;
    }

    // Setter para isActive
    public void setActive(boolean active) {
        this.isActive = active;
    }

    @Override
    public Object clone() {
        return new Employee(this);
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(this.employeeId);
        result = 31 * result + Integer.hashCode(this.restaurantId);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        Employee other = (Employee) o;
        return this.employeeId == other.employeeId;
    }

    @Override
    public String toString() {
        return String.format("Employee(id=%d, restaurantId=%d, name=%s, role=%s)",
                             this.employeeId, this.restaurantId, this.name, this.role);
    }

}
