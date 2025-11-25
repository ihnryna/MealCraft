package org.l5g7.mealcraft.app.products;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.l5g7.mealcraft.app.recipes.Recipe;
import org.l5g7.mealcraft.app.units.Entity.Unit;
import org.l5g7.mealcraft.app.user.User;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "product")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    private Unit defaultUnit;

    @ManyToOne
    private User ownerUser;

    private String imageUrl;

    @ManyToMany(mappedBy = "ingredients")
    private List<Recipe> recipes;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdAt;
}
