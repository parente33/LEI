package uminho.dss.business.order;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MenuItem {

    private int menuItemId;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private String category;
    private boolean isMenu;
    private boolean active;
    private Map<Integer, Double> ingredientQuantities;
    private Map<Integer, Boolean> ingredientOptional;
    private Map<Integer, Boolean> ingredientCustomizable;
    // Map<OriginalIngredientId, Map<AlternativeIngredientId, PriceModifier>>
    private Map<Integer, Map<Integer, BigDecimal>> ingredientAlternatives;

    public MenuItem(int menuItemId, String name, String description, BigDecimal basePrice,
            String category, boolean isMenu, boolean active) {
        this.menuItemId = menuItemId;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.category = category;
        this.isMenu = isMenu;
        this.active = active;
        this.ingredientQuantities = new HashMap<>();
        this.ingredientOptional = new HashMap<>();
        this.ingredientCustomizable = new HashMap<>();
        this.ingredientAlternatives = new HashMap<>();
    }

    public MenuItem(MenuItem other) {
        this.menuItemId = other.menuItemId;
        this.name = other.name;
        this.description = other.description;
        this.basePrice = other.basePrice;
        this.category = other.category;
        this.isMenu = other.isMenu;
        this.active = other.active;
        this.ingredientQuantities = new HashMap<>(other.ingredientQuantities);
        this.ingredientOptional = new HashMap<>(other.ingredientOptional);
        this.ingredientCustomizable = new HashMap<>(other.ingredientCustomizable);
        this.ingredientAlternatives = new HashMap<>();
        for (Map.Entry<Integer, Map<Integer, BigDecimal>> entry : other.ingredientAlternatives.entrySet()) {
            this.ingredientAlternatives.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
    }

    public int getMenuItemId() {
        return this.menuItemId;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public BigDecimal getBasePrice() {
        return this.basePrice;
    }

    public String getCategory() {
        return this.category;
    }

    public boolean isMenu() {
        return this.isMenu;
    }

    public boolean isActive() {
        return this.active;
    }

    public Set<Integer> getIngredientIds() {
        return this.ingredientQuantities.keySet();
    }

    public Double getIngredientQuantity(int ingredientId) {
        return this.ingredientQuantities.get(ingredientId);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void addIngredient(int ingredientId, double quantity) {
        this.ingredientQuantities.put(ingredientId, quantity);
    }

    public void removeIngredient(int ingredientId) {
        this.ingredientQuantities.remove(ingredientId);
        this.ingredientOptional.remove(ingredientId);
        this.ingredientCustomizable.remove(ingredientId);
        this.ingredientAlternatives.remove(ingredientId);
    }

    public void addIngredient(int ingredientId, double quantity, boolean isOptional, boolean isCustomizable) {
        this.ingredientQuantities.put(ingredientId, quantity);
        this.ingredientOptional.put(ingredientId, isOptional);
        this.ingredientCustomizable.put(ingredientId, isCustomizable);
    }

    public boolean isIngredientOptional(int ingredientId) {
        return this.ingredientOptional.getOrDefault(ingredientId, false);
    }

    public boolean isIngredientCustomizable(int ingredientId) {
        return this.ingredientCustomizable.getOrDefault(ingredientId, false);
    }

    public void addAlternative(int originalId, int alternativeId, BigDecimal priceModifier) {
        this.ingredientAlternatives.putIfAbsent(originalId, new HashMap<>());
        this.ingredientAlternatives.get(originalId).put(alternativeId, priceModifier);
    }

    public Map<Integer, BigDecimal> getAlternativesFor(int originalId) {
        return this.ingredientAlternatives.getOrDefault(originalId, new HashMap<>());
    }

    @Override
    public Object clone() {
        return new MenuItem(this);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.menuItemId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        MenuItem other = (MenuItem) o;
        return this.menuItemId == other.menuItemId;
    }

    @Override
    public String toString() {
        return String.format("MenuItem(id=%d, name=%s, price=%s, category=%s)",
                this.menuItemId, this.name, this.basePrice, this.category);
    }
}
