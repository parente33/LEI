package uminho.dss.data.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import uminho.dss.business.kitchen.WorkstationType;
import uminho.dss.data.AbstractDAO;

public class WorkstationTypeDAO extends AbstractDAO<WorkstationType> {

    private static WorkstationTypeDAO instance = null;

    private WorkstationTypeDAO() {
        super("workstation_types", "workstation_type_id");
    }

    public static WorkstationTypeDAO getInstance() {
        if (instance == null) {
            instance = new WorkstationTypeDAO();
        }
        return instance;
    }

    @Override
    public boolean containsValue(Object value) {
        boolean res = false;
        if (value instanceof WorkstationType && value != null) {
            WorkstationType type = (WorkstationType) value;
            WorkstationType inDb = this.get(String.valueOf(type.getId()));
            if (inDb != null) {
                res = inDb.getName().equals(type.getName());
            }
        }
        return res;
    }

    @Override
    public WorkstationType put(String key, WorkstationType value) {
        throw new UnsupportedOperationException("Workstation Types are read-only in this application version.");
    }

    @Override
    protected WorkstationType decodeTuple(ResultSet result) throws SQLException {
        return new WorkstationType(
                result.getInt("workstation_type_id"),
                result.getString("type_name"),
                result.getString("description")
        );
    }
}
