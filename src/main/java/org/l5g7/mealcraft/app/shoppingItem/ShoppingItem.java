package org.l5g7.mealcraft.app.shoppingItem;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.l5g7.mealcraft.app.products.Product;
import org.l5g7.mealcraft.app.user.User;

import java.util.List;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ShoppingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User userOwner;

    @ManyToOne
    private Product product;

    @Column(nullable = false)
    private Integer requiredQty;

    @Column(nullable = false)
    private Boolean status;  // to-buy - false, bought - true


}
