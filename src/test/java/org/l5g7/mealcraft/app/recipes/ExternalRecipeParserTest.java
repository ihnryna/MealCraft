package org.l5g7.mealcraft.app.recipes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.l5g7.mealcraft.app.recipeingredient.RecipeIngredientDto;
import org.l5g7.mealcraft.mealcraftstarterexternalrecipes.ExternalRecipe;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ExternalRecipeParserTest {

    @Test
    void toRecipeDto_convertsExternalRecipe() {
        ExternalRecipe external = new ExternalRecipe(
                1L,
                "Pasta Carbonara",
                "http://example.com/pasta.jpg",
                "2025-01-01",
                Arrays.asList("Spaghetti", "Eggs", "Bacon"),
                Arrays.asList("200g", "2", "100g")
        );

        RecipeDto result = ExternalRecipeParser.toRecipeDto(external);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Pasta Carbonara", result.getName());
        assertEquals("http://example.com/pasta.jpg", result.getImageUrl());
        assertEquals(3, result.getIngredients().size());
        assertEquals("Spaghetti", result.getIngredients().get(0).getProductName());
        assertEquals("Eggs", result.getIngredients().get(1).getProductName());
        assertEquals("Bacon", result.getIngredients().get(2).getProductName());
    }

    @Test
    void toRecipeDto_handlesNullIngredients() {
        ExternalRecipe external = new ExternalRecipe(
                1L,
                "Empty Recipe",
                "http://example.com/empty.jpg",
                "2025-01-01",
                null,
                null
        );

        RecipeDto result = ExternalRecipeParser.toRecipeDto(external);

        assertNotNull(result);
        assertEquals("Empty Recipe", result.getName());
        assertTrue(result.getIngredients().isEmpty());
    }

    @Test
    void toRecipeDto_skipsBlankIngredientNames() {
        ExternalRecipe external = new ExternalRecipe(
                1L,
                "Recipe",
                "http://example.com/recipe.jpg",
                "2025-01-01",
                Arrays.asList("Flour", "", null, "  ", "Sugar"),
                Arrays.asList("200g", "100g", "50g", "10g", "100g")
        );

        RecipeDto result = ExternalRecipeParser.toRecipeDto(external);

        assertEquals(2, result.getIngredients().size());
        assertEquals("Flour", result.getIngredients().get(0).getProductName());
        assertEquals("Sugar", result.getIngredients().get(1).getProductName());
    }

    @Test
    void toRecipeDto_handlesMismatchedListSizes() {
        ExternalRecipe external = new ExternalRecipe(
                1L,
                "Recipe",
                "http://example.com/recipe.jpg",
                "2025-01-01",
                Arrays.asList("Flour", "Sugar", "Eggs", "Milk", "Butter"),
                Arrays.asList("200g", "100g")
        );

        RecipeDto result = ExternalRecipeParser.toRecipeDto(external);

        assertEquals(2, result.getIngredients().size());
    }

    @Test
    void fillIngredientFromStrings_setsAllFields() {
        RecipeIngredientDto dto = new RecipeIngredientDto();

        ExternalRecipeParser.fillIngredientFromStrings(dto, "Flour", "200g");

        assertEquals("Flour", dto.getProductName());
        assertEquals(1.0, dto.getAmount());
        assertEquals("200g", dto.getUnitName());
    }

    @Test
    void fillIngredientFromStrings_handlesNullMeasure() {
        RecipeIngredientDto dto = new RecipeIngredientDto();

        ExternalRecipeParser.fillIngredientFromStrings(dto, "Salt", null);

        assertEquals("Salt", dto.getProductName());
        assertEquals(1.0, dto.getAmount());
        assertEquals("pc", dto.getUnitName());
    }

    @Test
    void normalizeMeasure_replacesCommaWithDot() {
        String result = ExternalRecipeParser.normalizeMeasure("2,5 kg");
        assertEquals("2.5 kg", result);
    }

    @Test
    void normalizeMeasure_convertsUnicodeFractions() {
        assertEquals("1 1/2 cups", ExternalRecipeParser.normalizeMeasure("1½ cups"));
        assertEquals("1/4 tsp", ExternalRecipeParser.normalizeMeasure("¼ tsp"));
        assertEquals("3/4 cup", ExternalRecipeParser.normalizeMeasure("¾ cup"));
        assertEquals("1/3 liter", ExternalRecipeParser.normalizeMeasure("⅓ liter"));
        assertEquals("2/3 oz", ExternalRecipeParser.normalizeMeasure("⅔ oz"));
    }

    @Test
    void normalizeMeasure_trimsWhitespace() {
        String result = ExternalRecipeParser.normalizeMeasure("  200   g  ");
        assertEquals("200 g", result);
    }

    @Test
    void normalizeMeasure_handlesNull() {
        String result = ExternalRecipeParser.normalizeMeasure(null);
        assertEquals("", result);
    }

    @Test
    void normalizeMeasure_handlesMultipleSpaces() {
        String result = ExternalRecipeParser.normalizeMeasure("2    1/2    cups");
        assertEquals("2 1/2 cups", result);
    }

    @Test
    void splitAmountAndUnit_splitsCorrectly() {
        String[] result = ExternalRecipeParser.splitAmountAndUnit("200 g");
        assertArrayEquals(new String[]{"200", "g"}, result);
    }

    @Test
    void splitAmountAndUnit_handlesFractions() {
        String[] result = ExternalRecipeParser.splitAmountAndUnit("1/2 cup");
        assertArrayEquals(new String[]{"1/2", "cup"}, result);
    }

    @Test
    void splitAmountAndUnit_handlesMixedNumbers() {
        String[] result = ExternalRecipeParser.splitAmountAndUnit("2 1/2 cups");
        assertArrayEquals(new String[]{"2 1/2", "cups"}, result);
    }

    @Test
    void splitAmountAndUnit_handlesDecimals() {
        String[] result = ExternalRecipeParser.splitAmountAndUnit("3.5 kg");
        assertArrayEquals(new String[]{"3.5", "kg"}, result);
    }

    @Test
    void splitAmountAndUnit_handlesNumberOnly() {
        String[] result = ExternalRecipeParser.splitAmountAndUnit("5");
        assertArrayEquals(new String[]{"5", null}, result);
    }

    @Test
    void splitAmountAndUnit_handlesUnitOnly() {
        String[] result = ExternalRecipeParser.splitAmountAndUnit("pinch");
        assertArrayEquals(new String[]{null, "pinch"}, result);
    }

    @Test
    void splitAmountAndUnit_handlesNull() {
        String[] result = ExternalRecipeParser.splitAmountAndUnit(null);
        assertArrayEquals(new String[]{null, null}, result);
    }

    @Test
    void splitAmountAndUnit_handlesEmpty() {
        String[] result = ExternalRecipeParser.splitAmountAndUnit("");
        assertArrayEquals(new String[]{null, null}, result);
    }

    @Test
    void parseAmountPart_parsesInteger() {
        Double result = ExternalRecipeParser.parseAmountPart("5");
        assertEquals(5.0, result);
    }

    @Test
    void parseAmountPart_parsesDecimal() {
        Double result = ExternalRecipeParser.parseAmountPart("3.5");
        assertEquals(3.5, result);
    }

    @Test
    void parseAmountPart_parsesFraction() {
        Double result = ExternalRecipeParser.parseAmountPart("1/2");
        assertEquals(0.5, result);
    }

    @Test
    void parseAmountPart_parsesMixedNumber() {
        Double result = ExternalRecipeParser.parseAmountPart("2 1/2");
        assertEquals(2.5, result);
    }

    @Test
    void parseAmountPart_handlesNull() {
        Double result = ExternalRecipeParser.parseAmountPart(null);
        assertEquals(1.0, result);
    }

    @Test
    void parseAmountPart_handlesEmpty() {
        Double result = ExternalRecipeParser.parseAmountPart("");
        assertEquals(1.0, result);
    }

    @Test
    void parseAmountPart_handlesBlank() {
        Double result = ExternalRecipeParser.parseAmountPart("   ");
        assertEquals(1.0, result);
    }

    @Test
    void parseFraction_calculatesCorrectly() {
        assertEquals(0.5, ExternalRecipeParser.parseFraction("1/2"));
        assertEquals(0.25, ExternalRecipeParser.parseFraction("1/4"));
        assertEquals(0.75, ExternalRecipeParser.parseFraction("3/4"));
        assertEquals(0.333333, ExternalRecipeParser.parseFraction("1/3"), 0.00001);
        assertEquals(0.666666, ExternalRecipeParser.parseFraction("2/3"), 0.00001);
    }

    @ParameterizedTest
    @CsvSource({
            "1/0, 0.0",
            "invalid, 0.0",
            "5, 0.0"
    })
    void parseFraction_returnsZeroForInvalidInput(String input, double expected) {
        Double result = ExternalRecipeParser.parseFraction(input);
        assertEquals(expected, result);
    }

    @Test
    void normalizeUnitName_returnsUnitTrimmed() {
        String result = ExternalRecipeParser.normalizeUnitName("  kg  ");
        assertEquals("kg", result);
    }

    @Test
    void normalizeUnitName_handlesNull() {
        String result = ExternalRecipeParser.normalizeUnitName(null);
        assertEquals("pc", result);
    }

    @Test
    void normalizeUnitName_handlesEmpty() {
        String result = ExternalRecipeParser.normalizeUnitName("");
        assertEquals("pc", result);
    }

    @Test
    void normalizeUnitName_handlesBlank() {
        String result = ExternalRecipeParser.normalizeUnitName("   ");
        assertEquals("pc", result);
    }

    @Test
    void integrationTest_complexMeasure() {
        RecipeIngredientDto dto = new RecipeIngredientDto();

        ExternalRecipeParser.fillIngredientFromStrings(dto, "Flour", "2½ cups");

        assertEquals("Flour", dto.getProductName());
        assertEquals(2.5, dto.getAmount());
        assertEquals("cups", dto.getUnitName());
    }

    @Test
    void integrationTest_measureWithComma() {
        RecipeIngredientDto dto = new RecipeIngredientDto();

        ExternalRecipeParser.fillIngredientFromStrings(dto, "Sugar", "1,5 kg");

        assertEquals("Sugar", dto.getProductName());
        assertEquals(1.5, dto.getAmount());
        assertEquals("kg", dto.getUnitName());
    }

    @Test
    void integrationTest_justNumber() {
        RecipeIngredientDto dto = new RecipeIngredientDto();

        ExternalRecipeParser.fillIngredientFromStrings(dto, "Eggs", "3");

        assertEquals("Eggs", dto.getProductName());
        assertEquals(3.0, dto.getAmount());
        assertEquals("pc", dto.getUnitName());
    }

    @Test
    void integrationTest_noAmount() {
        RecipeIngredientDto dto = new RecipeIngredientDto();

        ExternalRecipeParser.fillIngredientFromStrings(dto, "Salt", "pinch");

        assertEquals("Salt", dto.getProductName());
        assertEquals(1.0, dto.getAmount());
        assertEquals("pinch", dto.getUnitName());
    }

    @Test
    void integrationTest_multipleFractionsInMeasure() {
        RecipeIngredientDto dto = new RecipeIngredientDto();

        ExternalRecipeParser.fillIngredientFromStrings(dto, "Milk", "1¼ cups");

        assertEquals("Milk", dto.getProductName());
        assertEquals(1.25, dto.getAmount());
        assertEquals("cups", dto.getUnitName());
    }
}

