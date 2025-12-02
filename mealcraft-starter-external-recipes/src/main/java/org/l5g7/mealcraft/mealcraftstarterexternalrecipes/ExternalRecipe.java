package org.l5g7.mealcraft.mealcraftstarterexternalrecipes;

import java.util.List;

public record ExternalRecipe(
        Long id,
        String name,
        String imageUrl,
        String createdAt,
        List<String> ingredients,
        List<String> measures
) {}
