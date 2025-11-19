package org.l5g7.mealcraft.app.recipes;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.l5g7.mealcraft.app.products.Product;
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

    @ManyToMany
    @JoinTable(
            name = "recipe_product",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> ingredients;
}
