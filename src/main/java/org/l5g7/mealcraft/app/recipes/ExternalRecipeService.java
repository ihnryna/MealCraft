package org.l5g7.mealcraft.app.recipes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

@Service
public class ExternalRecipeService {

    private final RestClient defaultClient;
    private ObjectMapper objectMapper;

    @Autowired
    public ExternalRecipeService(RestClient defaultClient) {
        this.defaultClient = defaultClient;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Recipe getRandomRecipe() throws Exception {
        String randomRecipe = defaultClient.get()
                .uri("https://www.themealdb.com/api/json/v1/1/random.php")
                .retrieve()
                .body(String.class);

        JsonNode root = objectMapper.readTree(randomRecipe);
        JsonNode meal = root.path("meals").get(0);

        if (meal == null || meal.isMissingNode()) {
            throw new RuntimeException("Recipe not found in external API");
        }


        return Recipe.builder()
                .id(System.currentTimeMillis()) // генерація локального id
                .name(meal.path("strMeal").asText())
                .imageUrl(meal.path("strMealThumb").asText(null))
                .createdAt(LocalDateTime.now())
                .build();
    }


}
