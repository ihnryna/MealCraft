package org.l5g7.mealcraft.service;

import org.springframework.stereotype.Service;

@Service
public class RecipeService {

    private final ExternalRecipeService externalRecipeService;

    public RecipeService(ExternalRecipeService externalRecipeService) {
        this.externalRecipeService = externalRecipeService;
    }

}

