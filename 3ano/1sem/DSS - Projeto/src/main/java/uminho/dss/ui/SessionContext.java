package uminho.dss.ui;

import uminho.dss.business.IBusiness;

public class SessionContext {
    private IBusiness business;
    private String employeeName;
    private String role;
    private Integer restaurantId;
    private Integer kioskId;
    private String workstationCode;

    public SessionContext(IBusiness business) {
        this.business = business;
    }

    public IBusiness getBusiness() {
        return business;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Integer getKioskId() {
        return kioskId;
    }

    public void setKioskId(Integer kioskId) {
        this.kioskId = kioskId;
    }

    public String getWorkstationCode() {
        return workstationCode;
    }

    public void setWorkstationCode(String workstationCode) {
        this.workstationCode = workstationCode;
    }

    public boolean isAuthenticated() {
        return employeeName != null && role != null;
    }

    public boolean hasRole(String requiredRole) {
        return role != null && role.equals(requiredRole);
    }

    public void clear() {
        employeeName = null;
        role = null;
        restaurantId = null;
        kioskId = null;
        workstationCode = null;
    }
}
