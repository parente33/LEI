package uminho.dss.ui;

import uminho.dss.business.IBusiness;

public class UIManager {

    private IBusiness business;
    private SessionContext context;
    private ViewFactory factory;
    private UIRouter router;

    public UIManager(IBusiness business) {
        this.business = business;
        this.context = new SessionContext(business);
        this.factory = new ViewFactory(business);
        this.router = new UIRouter(factory, context);
    }

    public void start() {
        UIHelper.printHeader("SISTEMA DE GESTAO - FASTFOOD");
        router.showMainMenu();
        System.out.println("\n  Ate breve!\n");
    }
}
