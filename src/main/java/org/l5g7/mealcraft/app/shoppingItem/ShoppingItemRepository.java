package org.l5g7.mealcraft.app.shoppingItem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShoppingItemRepository extends JpaRepository<ShoppingItem, Long> {
    List<ShoppingItem> findByUserOwnerId(Long userId);
}
