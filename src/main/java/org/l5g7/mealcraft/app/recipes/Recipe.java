package org.l5g7.mealcraft.app.recipes;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.l5g7.mealcraft.app.recipeingredient.RecipeIngredient;
import org.l5g7.mealcraft.app.user.User;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "recipe")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User ownerUser;

    @ManyToOne
    private Recipe baseRecipe;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    private String imageUrl;

    @OneToMany(mappedBy = "recipe",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<RecipeIngredient> ingredients;
}
