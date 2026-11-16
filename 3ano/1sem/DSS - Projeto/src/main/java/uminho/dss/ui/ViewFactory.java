package uminho.dss.ui;

import uminho.dss.business.IBusiness;
import uminho.dss.ui.controller.*;

public class ViewFactory {

    private IBusiness business;

    public ViewFactory(IBusiness business) {
        this.business = business;
    }

    public KioskController createKioskController(int kioskId, int restaurantId) {
        return new KioskController(business, kioskId, restaurantId);
    }

    public EmployeeController createEmployeeController(String empName, String wsCode, int restId) {
        return new EmployeeController(business, empName, wsCode, restId);
    }

    public DeliveryController createDeliveryController(String deliveryType, int restId) {
        return new DeliveryController(business, deliveryType, restId);
    }

    public ManagerController createManagerController(String managerName, int restId) {
        return new ManagerController(business, managerName, restId);
    }

    public COOController createCOOController(String cooName) {
        return new COOController(business, cooName);
    }
}
