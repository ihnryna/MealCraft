package org.l5g7.mealcraft.app.recipes;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recipe")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*@ManyToOne
    private User ownerUser;*/

    @ManyToOne
    private Recipe baseRecipe;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    private String imageUrl;

    /*public Recipe(RecipeDto recipeDto) {
        this.id = (recipeDto.getId()!=null)? recipeDto.getId():0;
        this.ownerUser = recipeDto.getOwnerUserId();
        this.baseRecipe = recipeDto.getBaseRecipeId();
        this.name = recipeDto.getName();
        this.createdAt = recipeDto.getCreatedAt();
        this.imageUrl = recipeDto.getImageUrl();
    }*/
}
