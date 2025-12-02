package org.l5g7.mealcraft.unittest.shoppingitem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.l5g7.mealcraft.app.shoppingitem.ShoppingItemController;
import org.l5g7.mealcraft.app.shoppingitem.ShoppingItemDto;
import org.l5g7.mealcraft.app.shoppingitem.ShoppingItemService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingItemControllerTest {

    @Mock
    private ShoppingItemService shoppingItemService;

    @InjectMocks
    private ShoppingItemController controller;

    @Test
    void getAllShoppingItem_returnsList() {
        when(shoppingItemService.getAllShoppingItems()).thenReturn(List.of(ShoppingItemDto.builder().id(1L).name("Milk").build()));
        List<ShoppingItemDto> result = controller.getAllShoppingItem();
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(shoppingItemService, times(1)).getAllShoppingItems();
    }

    @Test
    void getShoppingItem_returnsDto() {
        ShoppingItemDto dto = ShoppingItemDto.builder().id(2L).name("Bread").build();
        when(shoppingItemService.getShoppingItemById(2L)).thenReturn(dto);
        ShoppingItemDto result = controller.getShoppingItem(2L);
        assertEquals(2L, result.getId());
        verify(shoppingItemService, times(1)).getShoppingItemById(2L);
    }

    @Test
    void getUserNotifications_returnsUserItems() {
        when(shoppingItemService.getUserShoppingItems(5L)).thenReturn(List.of(ShoppingItemDto.builder().id(3L).name("Eggs").build()));
        List<ShoppingItemDto> result = controller.getUserNotifications(5L);
        assertEquals(1, result.size());
        verify(shoppingItemService, times(1)).getUserShoppingItems(5L);
    }

    @Test
    void createShoppingItem_delegatesToService() {
        ShoppingItemDto dto = ShoppingItemDto.builder().id(10L).name("Cheese").build();
        controller.createShoppingItem(dto);
        verify(shoppingItemService, times(1)).createShoppingItem(dto);
    }

    @Test
    void updateShoppingItem_delegatesToService() {
        ShoppingItemDto dto = ShoppingItemDto.builder().id(11L).name("Butter").build();
        controller.updateShoppingItem(11L, dto);
        verify(shoppingItemService, times(1)).updateShoppingItem(11L, dto);
    }

    @Test
    void patchShoppingItem_delegatesToService() {
        ShoppingItemDto patch = ShoppingItemDto.builder().requiredQty(2.0).build();
        controller.patchShoppingItem(20L, patch);
        verify(shoppingItemService, times(1)).patchShoppingItem(20L, patch);
    }

    @Test
    void deleteShoppingItem_delegatesToService() {
        controller.deleteShoppingItem(30L);
        verify(shoppingItemService, times(1)).deleteShoppingItemById(30L);
    }

    @Test
    void toggleShoppingItemStatus_delegatesToService() {
        controller.toggleShoppingItemStatus(40L);
        verify(shoppingItemService, times(1)).toggleStatus(40L);
    }
}

