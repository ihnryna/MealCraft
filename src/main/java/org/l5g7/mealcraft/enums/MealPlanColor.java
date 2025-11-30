package org.l5g7.mealcraft.enums;

public enum MealPlanColor {
    GREEN("#C2FF47"),
    BLUE("#4766FF"),
    ORANGE("#FFB347"),
    PURPLE("#8547FF"),
    LIGHT_BLUE("#47C2FF");

    private final String hex;

    MealPlanColor(String hex) {
        this.hex = hex;
    }

    public String getHex() {
        return hex;
    }

    public static MealPlanColor fromHex(String hex) {
        for (MealPlanColor c : values()) {
            if (c.getHex().equalsIgnoreCase(hex)) return c;
        }
        throw new IllegalArgumentException("No enum constant with hex " + hex);
    }

}
