package org.l5g7.mealcraft.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.l5g7.mealcraft.entity.Recipe;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.awt.*;
import java.time.LocalDateTime;

@Service
public class ExternalRecipeService {

    private final RestClient defaultClient;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public ExternalRecipeService() {
        this.defaultClient = RestClient.create();
    }

    public ExternalRecipeService(RestClient.Builder clientBuilder) {
        this.defaultClient = clientBuilder
                .baseUrl("https://www.themealdb.com/api/json/v1/1")
                .build();
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
