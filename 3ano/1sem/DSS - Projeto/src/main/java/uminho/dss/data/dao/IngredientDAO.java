package uminho.dss.data.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import uminho.dss.business.order.Ingredient;
import uminho.dss.data.AbstractDAO;

public class IngredientDAO extends AbstractDAO<Ingredient> {

    private static IngredientDAO instance = null;

    private IngredientDAO() {
        super("ingredients", "ingredient_id");
    }

    public static IngredientDAO getInstance() {
        if (IngredientDAO.instance == null)
            IngredientDAO.instance = new IngredientDAO();
        return IngredientDAO.instance;
    }

    @Override
    public boolean containsValue(Object value) {
        if (value instanceof Ingredient && value != null) {
            Ingredient ing = (Ingredient) value;
            Ingredient found = this.get(String.valueOf(ing.getIngredientId()));
            return found != null && found.equals(ing);
        }
        return false;
    }

    @Override
    public Ingredient put(String key, Ingredient value) {
        try {
            Ingredient res = this.remove(key);

            try (PreparedStatement statement = this.connection.prepareStatement(
                    "INSERT INTO ingredients(name, allergen_type, unit_type, active, workstation_type_id) VALUES (?, ?, ?, ?, ?)")) {

                statement.setString(1, value.getName());
                statement.setString(2, value.getAllergenType());
                statement.setString(3, value.getUnitType());
                statement.setBoolean(4, value.isActive());

                if (value.getWorkstationTypeId() > 0) {
                    statement.setInt(5, value.getWorkstationTypeId());
                } else {
                    statement.setNull(5, Types.INTEGER);
                }

                statement.executeUpdate();
            }
            return res;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Ingredient decodeTuple(ResultSet result) throws SQLException {
        int ingredientId = result.getInt("ingredient_id");
        String name = result.getString("name");
        String allergenType = result.getString("allergen_type");
        String unitType = result.getString("unit_type");
        boolean active = result.getBoolean("active");

        // Se a coluna for NULL, getInt devolve 0, o que funciona bem com a nossa lógica
        int workstationTypeId = result.getInt("workstation_type_id");

        return new Ingredient(ingredientId, name, allergenType, unitType, active, workstationTypeId);
    }
}
