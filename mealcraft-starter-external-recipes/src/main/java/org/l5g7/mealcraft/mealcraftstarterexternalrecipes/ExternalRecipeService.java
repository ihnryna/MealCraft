package org.l5g7.mealcraft.mealcraftstarterexternalrecipes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

public class ExternalRecipeService implements RecipeProvider{

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String url;

    public ExternalRecipeService(RestClient restClient, ObjectMapper objectMapper, String url) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.url = url;
    }

    @Override
    public ExternalRecipe getRandomRecipe() throws Exception {
        String body = restClient.get().uri(url).retrieve().body(String.class);
        JsonNode meal = objectMapper.readTree(body).path("meals").get(0);
        if (meal == null || meal.isMissingNode()) throw new RuntimeException("Recipe not found");
        return new ExternalRecipe(
                System.currentTimeMillis(),
                meal.path("strMeal").asText(),
                meal.path("strMealThumb").asText(null),
                LocalDateTime.now().toString()
        );
    }
}
