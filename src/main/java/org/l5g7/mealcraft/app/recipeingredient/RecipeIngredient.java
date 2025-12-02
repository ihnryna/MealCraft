package org.l5g7.mealcraft.app.recipeingredient;

import jakarta.persistence.*;
import lombok.*;
import org.l5g7.mealcraft.app.products.Product;
import org.l5g7.mealcraft.app.recipes.Recipe;

@Entity
@Table(name = "recipe_ingredient")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "recipe")
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Double amount;
}

