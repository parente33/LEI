package uminho.dss.data.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import uminho.dss.business.kitchen.Employee;
import uminho.dss.data.AbstractDAO;

public class EmployeeDAO extends AbstractDAO<Employee> {

    private static EmployeeDAO instance = null;

    private EmployeeDAO() {
        super("employees", "employee_id");
    }

    public static EmployeeDAO getInstance() {
        if (EmployeeDAO.instance == null)
            EmployeeDAO.instance = new EmployeeDAO();

        return EmployeeDAO.instance;
    }

    @Override
    public boolean containsValue(Object value) {
        boolean res = false;
        if (value instanceof Employee && value != null) {
            Employee employee = this.get(String.valueOf(((Employee) value).getEmployeeId()));
            res = value.equals(employee);
        }
        return res;
    }

    @Override
    public Employee put(String key, Employee value) {
        try {
            Employee res = this.remove(key);

            try (PreparedStatement statement = this.connection.prepareStatement(
                    "INSERT INTO employees(restaurant_id, name, role, is_active) VALUES (?, ?, ?, ?)")
            ) {
                statement.setInt(1, value.getRestaurantId());
                statement.setString(2, value.getName());
                statement.setString(3, value.getRole());
                statement.setBoolean(4, value.isActive());
                statement.executeUpdate();
            }

            return res;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Employee decodeTuple(ResultSet result) throws SQLException {
        int employeeId = result.getInt("employee_id");
        int restaurantId = result.getInt("restaurant_id");
        String name = result.getString("name");
        String role = result.getString("role");
        boolean isActive = result.getBoolean("is_active");

        return new Employee(employeeId, restaurantId, name, role, isActive);
    }

    public String authenticate(String username, String password) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "SELECT e.name, a.password_hash " +
                        "FROM employees e " +
                        "INNER JOIN employee_authentication a ON e.employee_id = a.employee_id " +
                        "WHERE a.username = ?")) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    String storedHash = result.getString("password_hash");
                    String name = result.getString("name");

                    if (org.mindrot.jbcrypt.BCrypt.checkpw(password, storedHash)) {
                        return name;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
