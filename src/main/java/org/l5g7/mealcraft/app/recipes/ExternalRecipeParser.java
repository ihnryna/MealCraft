package org.l5g7.mealcraft.app.recipes;

import org.l5g7.mealcraft.app.recipeingredient.RecipeIngredientDto;
import org.l5g7.mealcraft.mealcraftstarterexternalrecipes.ExternalRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExternalRecipeParser {

    private ExternalRecipeParser() {
    }

    public static RecipeDto toRecipeDto(ExternalRecipe external) {

        List<RecipeIngredientDto> ingredients = new ArrayList<>();

        List<String> names = external.ingredients();
        List<String> measures = external.measures();

        if (names != null && measures != null) {
            int size = Math.min(names.size(), measures.size());
            for (int i = 0; i < size; i++) {
                String name = names.get(i);
                String measure = measures.get(i);

                if (name == null || name.isBlank()) {
                    continue;
                }

                RecipeIngredientDto dto = new RecipeIngredientDto();
                fillIngredientFromStrings(dto, name, measure);
                ingredients.add(dto);
            }
        }

        return RecipeDto.builder()
                .id(external.id())
                .name(external.name())
                .imageUrl(external.imageUrl())
                .ingredients(ingredients)
                .build();
    }

    public static void fillIngredientFromStrings(RecipeIngredientDto dto, String ingredientName, String rawMeasure) {

        String normalized = normalizeMeasure(rawMeasure);
        String[] parts = splitAmountAndUnit(normalized);
        Double amount = parseAmountPart(parts[0]);
        String unit = normalizeUnitName(parts[1]);

        dto.setProductName(ingredientName);
        dto.setAmount(amount);
        dto.setUnitName(unit);
    }

    public static String normalizeMeasure(String raw) {
        if (raw == null) {
            return "";
        }

        String s = raw.trim().replace(',', '.');

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '½' -> sb.append(" 1/2");
                case '¼' -> sb.append(" 1/4");
                case '¾' -> sb.append(" 3/4");
                case '⅓' -> sb.append(" 1/3");
                case '⅔' -> sb.append(" 2/3");
                default -> sb.append(c);
            }
        }

        String result = sb.toString();
        return result.replaceAll("\\s+", " ").trim();
    }

    public static String[] splitAmountAndUnit(String normalized) {
        if (normalized == null || normalized.isEmpty()) {
            return new String[]{null, null};
        }

        String regex = "^([0-9]+/[0-9]+|[0-9]+(?:\\.[0-9]+)?(?:\\s+[0-9]+/[0-9]+)?)\\s*(.*)$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(normalized);

        if (m.matches()) {
            String numberPart = m.group(1);
            String unitPart = m.group(2).trim();
            if (unitPart.isEmpty()) {
                unitPart = null;
            }
            return new String[]{numberPart, unitPart};
        } else {
            return new String[]{null, normalized};
        }
    }

    public static Double parseAmountPart(String numberPart) {
        if (numberPart == null || numberPart.isBlank()) {
            return 1.0;
        }

        String s = numberPart.trim();

        if (s.contains(" ")) {
            String[] parts = s.split("\\s+");
            double base = Double.parseDouble(parts[0]);
            double frac = 0.0;
            if (parts.length > 1 && parts[1].contains("/")) {
                frac = parseFraction(parts[1]);
            }
            return base + frac;
        }

        if (s.contains("/")) {
            return parseFraction(s);
        }

        return Double.parseDouble(s);
    }

    public static double parseFraction(String frac) {
        String[] p = frac.split("/");
        if (p.length != 2) {
            return 0.0;
        }
        double num = Double.parseDouble(p[0]);
        double den = Double.parseDouble(p[1]);
        if (den == 0.0) {
            return 0.0;
        }
        return num / den;
    }

    public static String normalizeUnitName(String rawUnit) {
        if (rawUnit == null || rawUnit.isBlank()) {
            return "pc";
        }
        return rawUnit.trim();
    }
}