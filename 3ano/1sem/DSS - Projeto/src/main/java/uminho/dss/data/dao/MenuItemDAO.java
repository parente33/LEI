package uminho.dss.data.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import uminho.dss.business.order.MenuItem;
import uminho.dss.data.AbstractDAO;

public class MenuItemDAO extends AbstractDAO<MenuItem> {

    private static MenuItemDAO instance = null;

    private MenuItemDAO() {
        super("menu_items", "menu_item_id");
    }

    public static MenuItemDAO getInstance() {
        if (MenuItemDAO.instance == null)
            MenuItemDAO.instance = new MenuItemDAO();

        return MenuItemDAO.instance;
    }

    @Override
    public boolean containsValue(Object value) {
        boolean res = false;
        if (value instanceof MenuItem && value != null) {
            MenuItem menuItem = this.get(String.valueOf(((MenuItem) value).getMenuItemId()));
            res = value.equals(menuItem);
        }
        return res;
    }

    @Override
    public MenuItem put(String key, MenuItem value) {
        boolean turnAutoCommitBackOn = false;
        try {
            turnAutoCommitBackOn = this.connection.getAutoCommit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            this.connection.setAutoCommit(false);
            MenuItem res = this.remove(key);

            try (PreparedStatement statement = this.connection.prepareStatement(
                    "INSERT INTO menu_items(name, description, base_price, category, is_menu, active) VALUES (?, ?, ?, ?, ?, ?)")) {

                statement.setString(1, value.getName());
                statement.setString(2, value.getDescription());
                statement.setBigDecimal(3, value.getBasePrice());
                statement.setString(4, value.getCategory());
                statement.setBoolean(5, value.isMenu());
                statement.setBoolean(6, value.isActive());
                statement.executeUpdate();
            }

            for (Integer ingredientId : value.getIngredientIds()) {
                try (PreparedStatement statement = this.connection.prepareStatement(
                        "INSERT INTO menu_item_ingredients(menu_item_id, ingredient_id, quantity, is_optional, is_customizable) VALUES (?, ?, ?, ?, ?)")) {

                    statement.setInt(1, value.getMenuItemId());
                    statement.setInt(2, ingredientId);
                    statement.setDouble(3, value.getIngredientQuantity(ingredientId));
                    statement.setBoolean(4, value.isIngredientOptional(ingredientId));
                    statement.setBoolean(5, value.isIngredientCustomizable(ingredientId));
                    statement.executeUpdate();
                }
                for (Map.Entry<Integer, BigDecimal> entry : value.getAlternativesFor(ingredientId).entrySet()) {
                    try (PreparedStatement statement = this.connection.prepareStatement(
                            "INSERT INTO menu_item_alternatives(menu_item_id, original_ingredient_id, alternative_ingredient_id, price_modifier) VALUES (?, ?, ?, ?)")) {
                        statement.setInt(1, value.getMenuItemId());
                        statement.setInt(2, ingredientId);
                        statement.setInt(3, entry.getKey());
                        statement.setBigDecimal(4, entry.getValue());
                        statement.executeUpdate();
                    }
                }
            }

            this.connection.commit();
            return res;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (turnAutoCommitBackOn)
                    this.connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected MenuItem decodeTuple(ResultSet result) throws SQLException {
        int menuItemId = result.getInt("menu_item_id");
        String name = result.getString("name");
        String description = result.getString("description");
        BigDecimal basePrice = result.getBigDecimal("base_price");
        String category = result.getString("category");
        boolean isMenu = result.getBoolean("is_menu");
        boolean active = result.getBoolean("active");

        MenuItem menuItem = new MenuItem(menuItemId, name, description, basePrice,
                category, isMenu, active);

        try (PreparedStatement statement = this.connection.prepareStatement(
                "SELECT ingredient_id, quantity, is_optional, is_customizable FROM menu_item_ingredients WHERE menu_item_id=?")) {

            statement.setInt(1, menuItemId);
            ResultSet ingredientSet = statement.executeQuery();

            while (ingredientSet.next()) {
                int ingredientId = ingredientSet.getInt("ingredient_id");
                double quantity = ingredientSet.getDouble("quantity");
                boolean isOptional = ingredientSet.getBoolean("is_optional");
                boolean isCustomizable = ingredientSet.getBoolean("is_customizable");
                menuItem.addIngredient(ingredientId, quantity, isOptional, isCustomizable);
            }
        }

        try (PreparedStatement statement = this.connection.prepareStatement(
                "SELECT original_ingredient_id, alternative_ingredient_id, price_modifier FROM menu_item_alternatives WHERE menu_item_id=?")) {

            statement.setInt(1, menuItemId);
            ResultSet altSet = statement.executeQuery();

            while (altSet.next()) {
                int originalId = altSet.getInt("original_ingredient_id");
                int alternativeId = altSet.getInt("alternative_ingredient_id");
                BigDecimal modifier = altSet.getBigDecimal("price_modifier");
                menuItem.addAlternative(originalId, alternativeId, modifier);
            }
        }

        return menuItem;
    }
}
