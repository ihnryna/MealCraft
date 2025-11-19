package org.l5g7.mealcraft.app.shoppingitem;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shopping-items")
public class ShoppingItemController {

    private final ShoppingItemService shoppingItemService;

    public ShoppingItemController(ShoppingItemService shoppingItemService) {
        this.shoppingItemService = shoppingItemService;
    }

    @GetMapping
    public List<ShoppingItemDto> getAllShoppingItem() {
        return shoppingItemService.getAllShoppingItems();
    }


    @GetMapping("/{id}")
    public ShoppingItemDto getShoppingItem(@PathVariable Long id) {
       return shoppingItemService.getShoppingItemById(id);
    }

    @GetMapping("/getUserShoppingItems/{id}")
    public List<ShoppingItemDto> getUserNotifications(@PathVariable Long id) {
        return shoppingItemService.getUserShoppingItems(id);
    }

    @PostMapping
    public void createShoppingItem(@Valid @RequestBody ShoppingItemDto shoppingItemDto) {
        shoppingItemService.createShoppingItem(shoppingItemDto);
    }

    @PutMapping("/{id}")
    public void updateShoppingItem(@PathVariable Long id, @Valid @RequestBody ShoppingItemDto updatedShoppingItem) {
        shoppingItemService.updateShoppingItem(id, updatedShoppingItem);

    }
    @PatchMapping("/{id}")
    public void patchShoppingItem(@PathVariable Long id, @RequestBody ShoppingItemDto partialUpdate) {
        shoppingItemService.patchShoppingItem(id, partialUpdate);
    }

    @DeleteMapping("/{id}")
    public void deleteShoppingItem(@PathVariable Long id) {
        shoppingItemService.deleteShoppingItemById(id);
    }

    @PatchMapping("/toggle/{id}")
    public void toggleShoppingItemStatus(@PathVariable Long id) {
        shoppingItemService.toggleStatus(id);
    }


}
