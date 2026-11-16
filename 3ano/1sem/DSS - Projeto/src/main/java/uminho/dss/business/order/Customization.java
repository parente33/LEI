package uminho.dss.business.order;

public class Customization {

    private int customizationId;
    private int orderItemId;
    private String customizationType;
    private Integer ingredientId;
    private Integer alternativeIngredientId;
    private String cookingInstruction;

    public Customization(int customizationId, int orderItemId, String customizationType,
           Integer ingredientId, Integer alternativeIngredientId,
           String cookingInstruction) {
        this.customizationId = customizationId;
        this.orderItemId = orderItemId;
        this.customizationType = customizationType;
        this.ingredientId = ingredientId;
        this.alternativeIngredientId = alternativeIngredientId;
        this.cookingInstruction = cookingInstruction;
    }

    public Customization(Customization other) {
        this.customizationId = other.customizationId;
        this.orderItemId = other.orderItemId;
        this.customizationType = other.customizationType;
        this.ingredientId = other.ingredientId;
        this.alternativeIngredientId = other.alternativeIngredientId;
        this.cookingInstruction = other.cookingInstruction;
    }

    public int getCustomizationId() {
        return this.customizationId;
    }

    public int getOrderItemId() {
        return this.orderItemId;
    }

    public String getCustomizationType() {
        return this.customizationType;
    }

    public Integer getIngredientId() {
        return this.ingredientId;
    }

    public Integer getAlternativeIngredientId() {
        return this.alternativeIngredientId;
    }

    public String getCookingInstruction() {
        return this.cookingInstruction;
    }

    @Override
    public Object clone() {
        return new Customization(this);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.customizationId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        Customization other = (Customization) o;
        return this.customizationId == other.customizationId;
    }

    @Override
    public String toString() {
        return String.format("Customization(id=%d, type=%s)",
                this.customizationId, this.customizationType);
    }
}
