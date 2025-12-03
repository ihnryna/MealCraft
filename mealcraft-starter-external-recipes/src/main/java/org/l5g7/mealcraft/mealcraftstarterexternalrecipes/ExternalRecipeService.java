package org.l5g7.mealcraft.mealcraftstarterexternalrecipes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ExternalRecipeService implements RecipeProvider {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String url;

    public ExternalRecipeService(RestClient restClient, ObjectMapper objectMapper, String url) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.url = url;
    }

    @Override
    public ExternalRecipe getRandomRecipe() throws NoSuchElementException {
        String body = restClient.get()
                .uri(url)
                .retrieve()
                .body(String.class);

        JsonNode root;
        try {
            root = objectMapper.readTree(body);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("JSON pares failed",e);
        }

        JsonNode meal = root.path("meals").get(0);
        if (meal == null || meal.isMissingNode()) {
            throw new NoSuchElementException("Recipe not found");
        }

        List<String> ingredients = new ArrayList<>();
        List<String> measures = new ArrayList<>();

        for (int i = 1; i <= 20; i++) {
            String ing = meal.path("strIngredient" + i).asText();
            String meas = meal.path("strMeasure" + i).asText();

            if (ing != null && !ing.isBlank()) {
                ingredients.add(ing);
                measures.add(meas != null ? meas : "");
            }
        }

        return new ExternalRecipe(
                System.currentTimeMillis(),
                meal.path("strMeal").asText(),
                meal.path("strMealThumb").asText(null),
                LocalDateTime.now().toString(),
                ingredients,
                measures
        );
    }
}
