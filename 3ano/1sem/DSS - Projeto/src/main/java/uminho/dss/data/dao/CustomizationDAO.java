package uminho.dss.data.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import uminho.dss.business.order.Customization;
import uminho.dss.data.AbstractDAO;

public class CustomizationDAO extends AbstractDAO<Customization> {

    private static CustomizationDAO instance = null;

    private CustomizationDAO() {
        super("order_item_customizations", "customization_id");
    }

    public static CustomizationDAO getInstance() {
        if (CustomizationDAO.instance == null)
            CustomizationDAO.instance = new CustomizationDAO();

        return CustomizationDAO.instance;
    }

    @Override
    public boolean containsValue(Object value) {
        boolean res = false;
        if (value instanceof Customization && value != null) {
            Customization customization = this.get(String.valueOf(((Customization) value).getCustomizationId()));
            res = value.equals(customization);
        }
        return res;
    }

    @Override
    public Customization put(String key, Customization value) {
        try {
            Customization res = this.remove(key);

            try (PreparedStatement statement = this.connection.prepareStatement(
                    "INSERT INTO order_item_customizations(order_item_id, customization_type, ingredient_id, alternative_ingredient_id, cooking_instruction) VALUES (?, ?, ?, ?, ?)")) {

                statement.setInt(1, value.getOrderItemId());
                statement.setString(2, value.getCustomizationType());

                if (value.getIngredientId() != null)
                    statement.setInt(3, value.getIngredientId());
                else
                    statement.setNull(3, java.sql.Types.INTEGER);

                if (value.getAlternativeIngredientId() != null)
                    statement.setInt(4, value.getAlternativeIngredientId());
                else
                    statement.setNull(4, java.sql.Types.INTEGER);

                statement.setString(5, value.getCookingInstruction());
                statement.executeUpdate();
            }

            return res;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Customization decodeTuple(ResultSet result) throws SQLException {
        int customizationId = result.getInt("customization_id");
        int orderItemId = result.getInt("order_item_id");
        String customizationType = result.getString("customization_type");
        Integer ingredientId = result.getObject("ingredient_id", Integer.class);
        Integer alternativeIngredientId = result.getObject("alternative_ingredient_id", Integer.class);
        String cookingInstruction = result.getString("cooking_instruction");

        return new Customization(customizationId, orderItemId, customizationType,
                ingredientId, alternativeIngredientId, cookingInstruction);
    }
}
