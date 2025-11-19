package org.l5g7.mealcraft.app.shoppingitem;
import java.util.List;

public interface ShoppingItemService {

    List<ShoppingItemDto> getAllShoppingItems();
    List<ShoppingItemDto> getUserShoppingItems(Long userId);
    ShoppingItemDto getShoppingItemById(Long id);
    void createShoppingItem(ShoppingItemDto shoppingItemDto);
    void updateShoppingItem(Long id, ShoppingItemDto shoppingItemDto);
    void patchShoppingItem(Long id, ShoppingItemDto patch);
    void deleteShoppingItemById(Long id);
    void toggleStatus(Long id);

}

