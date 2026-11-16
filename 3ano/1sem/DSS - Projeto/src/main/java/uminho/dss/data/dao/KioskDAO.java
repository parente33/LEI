package uminho.dss.data.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import uminho.dss.business.order.Kiosk;
import uminho.dss.data.AbstractDAO;

public class KioskDAO extends AbstractDAO<Kiosk> {

    private static KioskDAO instance = null;

    private KioskDAO() {
        super("kiosks", "kiosk_id");
    }

    public static KioskDAO getInstance() {
        if (KioskDAO.instance == null)
            KioskDAO.instance = new KioskDAO();

        return KioskDAO.instance;
    }

    @Override
    public boolean containsValue(Object value) {
        boolean res = false;
        if (value instanceof Kiosk && value != null) {
            Kiosk kiosk = this.get(String.valueOf(((Kiosk) value).getKioskId()));
            res = value.equals(kiosk);
        }
        return res;
    }

    @Override
    public Kiosk put(String key, Kiosk value) {
        try {
            Kiosk res = this.remove(key);

            try (PreparedStatement statement = this.connection.prepareStatement(
                    "INSERT INTO kiosks(restaurant_id, status) VALUES (?, ?)")) {

                statement.setInt(1, value.getRestaurantId());
                statement.setString(2, value.getStatus());
                statement.executeUpdate();
            }

            return res;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Kiosk decodeTuple(ResultSet result) throws SQLException {
        int kioskId = result.getInt("kiosk_id");
        int restaurantId = result.getInt("restaurant_id");
        String status = result.getString("status");

        return new Kiosk(kioskId, restaurantId, status);
    }
}
