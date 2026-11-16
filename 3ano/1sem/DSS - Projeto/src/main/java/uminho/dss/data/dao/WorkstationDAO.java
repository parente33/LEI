package uminho.dss.data.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import uminho.dss.business.kitchen.Workstation;
import uminho.dss.data.AbstractDAO;

public class WorkstationDAO extends AbstractDAO<Workstation> {

    private static WorkstationDAO instance = null;

    private WorkstationDAO() {
        super("workstations", "workstation_id");
    }

    public static WorkstationDAO getInstance() {
        if (WorkstationDAO.instance == null) {
            WorkstationDAO.instance = new WorkstationDAO();
        }
        return WorkstationDAO.instance;
    }

    @Override
    public boolean containsValue(Object value) {
        if (value instanceof Workstation && value != null) {
            Workstation w = (Workstation) value;
            Workstation found = this.get(String.valueOf(w.getWorkstationId()));
            return found != null && found.equals(w);
        }
        return false;
    }

    @Override
    public Workstation put(String key, Workstation value) {
        try {
            Workstation res = this.remove(key);
            try (PreparedStatement statement = this.connection.prepareStatement(
                    "INSERT INTO workstations(restaurant_id, workstation_type_id, is_active) VALUES (?, ?, ?)")) {

                statement.setInt(1, value.getRestaurantId());
                statement.setInt(2, value.getWorkstationTypeId());
                statement.setBoolean(3, value.isActive());
                statement.executeUpdate();
            }

            return res;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Workstation decodeTuple(ResultSet result) throws SQLException {
        int workstationId = result.getInt("workstation_id");
        int restaurantId = result.getInt("restaurant_id");
        int workstationTypeId = result.getInt("workstation_type_id"); // Ler nova coluna
        boolean isActive = result.getBoolean("is_active");

        return new Workstation(workstationId, restaurantId, workstationTypeId, isActive);
    }
}
