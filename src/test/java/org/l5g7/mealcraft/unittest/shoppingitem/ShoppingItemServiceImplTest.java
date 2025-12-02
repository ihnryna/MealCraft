package org.l5g7.mealcraft.unittest.shoppingitem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.l5g7.mealcraft.app.products.Product;
import org.l5g7.mealcraft.app.products.ProductRepository;
import org.l5g7.mealcraft.app.shoppingitem.ShoppingItem;
import org.l5g7.mealcraft.app.shoppingitem.ShoppingItemDto;
import org.l5g7.mealcraft.app.shoppingitem.ShoppingItemRepository;
import org.l5g7.mealcraft.app.shoppingitem.ShoppingItemServiceImpl;
import org.l5g7.mealcraft.app.user.User;
import org.l5g7.mealcraft.app.user.UserRepository;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingItemServiceImplTest {

    @Mock
    private ShoppingItemRepository shoppingItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ShoppingItemServiceImpl service;

    private User user;
    private Product product;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        product = new Product();
        product.setId(2L);
        product.setName("Milk");
    }

    @Test
    void createShoppingItem_savesEntity() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));

        ShoppingItemDto dto = ShoppingItemDto.builder()
                .id(100L)
                .userOwnerId(1L)
                .productId(2L)
                .requiredQty(2.0)
                .status(false)
                .build();

        service.createShoppingItem(dto);

        ArgumentCaptor<ShoppingItem> captor = ArgumentCaptor.forClass(ShoppingItem.class);
        verify(shoppingItemRepository, times(1)).save(captor.capture());
        ShoppingItem saved = captor.getValue();
        assertEquals(100L, saved.getId());
        assertEquals(user, saved.getUserOwner());
        assertEquals(product, saved.getProduct());
        assertEquals(2.0, saved.getRequiredQty());
        assertEquals(false, saved.getStatus());
    }

    @Test
    void createShoppingItem_throwsWhenUserMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        ShoppingItemDto dto = ShoppingItemDto.builder().userOwnerId(1L).productId(2L).build();
        assertThrows(EntityDoesNotExistException.class, () -> service.createShoppingItem(dto));
        verify(shoppingItemRepository, never()).save(any());
    }

    @Test
    void updateShoppingItem_throwsWhenItemNotFound() {
        when(shoppingItemRepository.findById(100L)).thenReturn(Optional.empty());
        ShoppingItemDto dto = ShoppingItemDto.builder().userOwnerId(1L).productId(2L).build();
        assertThrows(EntityDoesNotExistException.class, () -> service.updateShoppingItem(100L, dto));
    }

    @Test
    void toggleStatus_flipsBooleanAndSetsBoughtAt() {
        ShoppingItem existing = ShoppingItem.builder()
                .id(100L)
                .userOwner(user)
                .product(product)
                .requiredQty(1.0)
                .status(false)
                .build();
        when(shoppingItemRepository.findById(100L)).thenReturn(Optional.of(existing));

        service.toggleStatus(100L);

        ArgumentCaptor<ShoppingItem> captor = ArgumentCaptor.forClass(ShoppingItem.class);
        verify(shoppingItemRepository, times(1)).save(captor.capture());
        ShoppingItem saved = captor.getValue();
        assertTrue(saved.getStatus());
        assertNotNull(saved.getBoughtAt());
    }

    @Test
    void toggleStatus_throwsWhenNotFound() {
        when(shoppingItemRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(EntityDoesNotExistException.class, () -> service.toggleStatus(999L));
    }
}

