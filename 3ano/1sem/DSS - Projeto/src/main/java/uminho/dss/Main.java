package uminho.dss;

import uminho.dss.business.BusinessFacade;
import uminho.dss.business.IBusiness;
import uminho.dss.ui.UIManager;

public class Main {

    public static void main(String[] args) {
        IBusiness business = new BusinessFacade();
        UIManager uiManager = new UIManager(business);
        uiManager.start();
    }
}
