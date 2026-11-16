package uminho.dss.business.order;

public class Ingredient {

    private int ingredientId;
    private String name;
    private String allergenType;
    private String unitType;
    private boolean active;
    private int workstationTypeId;

    public Ingredient(int ingredientId, String name, String allergenType,
                      String unitType, boolean active, int workstationTypeId) {
        this.ingredientId = ingredientId;
        this.name = name;
        this.allergenType = allergenType;
        this.unitType = unitType;
        this.active = active;
        this.workstationTypeId = workstationTypeId;
    }

    // Construtor de cópia
    public Ingredient(Ingredient other) {
        this.ingredientId = other.ingredientId;
        this.name = other.name;
        this.allergenType = other.allergenType;
        this.unitType = other.unitType;
        this.active = other.active;
        this.workstationTypeId = other.workstationTypeId;
    }

    public int getIngredientId() { return ingredientId; }
    public String getName() { return name; }
    public String getAllergenType() { return allergenType; }
    public String getUnitType() { return unitType; }
    public boolean isActive() { return active; }

    public int getWorkstationTypeId() { return workstationTypeId; } // Novo Getter

    public void setActive(boolean active) { this.active = active; }

    @Override
    public Object clone() { return new Ingredient(this); }

    @Override
    public int hashCode() { return Integer.hashCode(ingredientId); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return ingredientId == that.ingredientId;
    }

    @Override
    public String toString() {
        return String.format("Ingredient(id=%d, name=%s, stationType=%d)",
                ingredientId, name, workstationTypeId);
    }
}
