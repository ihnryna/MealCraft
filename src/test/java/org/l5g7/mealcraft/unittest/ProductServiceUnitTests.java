package org.l5g7.mealcraft.unittest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.l5g7.mealcraft.app.products.Product;
import org.l5g7.mealcraft.app.products.ProductDto;
import org.l5g7.mealcraft.app.products.ProductRepository;
import org.l5g7.mealcraft.app.products.ProductServiceImpl;
import org.l5g7.mealcraft.app.recipes.Recipe;
import org.l5g7.mealcraft.app.recipes.RecipeRepository;
import org.l5g7.mealcraft.app.units.Entity.Unit;
import org.l5g7.mealcraft.app.units.interfaces.UnitRepository;
import org.l5g7.mealcraft.app.user.User;
import org.l5g7.mealcraft.app.user.UserRepository;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("java:S5786")
public class ProductServiceUnitTests {

    private ProductRepository productRepository;
    private UnitRepository unitRepository;
    private UserRepository userRepository;
    private RecipeRepository recipeRepository;
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        unitRepository = mock(UnitRepository.class);
        userRepository = mock(UserRepository.class);
        recipeRepository = mock(RecipeRepository.class);

        productService = new ProductServiceImpl(
                productRepository,
                unitRepository,
                userRepository,
                recipeRepository
        );
    }

    @Test
    void getProductById_existingProduct_returnsDto() {
        Unit unit = Unit.builder().id(1L).name("kg").build();
        User owner = User.builder()
                .id(1L)
                .username("user")
                .email("user@example.com")
                .password("password")
                .build();

        Product product = Product.builder()
                .id(10L)
                .name("Tomato")
                .defaultUnit(unit)
                .ownerUser(owner)
                .build();

        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        ProductDto dto = productService.getProductById(10L);

        assertEquals(10L, dto.getId());
        assertEquals("Tomato", dto.getName());
        assertEquals(1L, dto.getDefaultUnitId());
        assertEquals(1L, dto.getOwnerUserId());
    }

    @Test
    void createProduct_withValidDto_savesProduct() {
        Unit unit = Unit.builder().id(2L).name("liter").build();
        User owner = User.builder()
                .id(1L)
                .username("user")
                .email("user@example.com")
                .password("password")
                .build();

        when(unitRepository.findById(2L)).thenReturn(Optional.of(unit));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        ProductDto dto = ProductDto.builder()
                .name("Milk")
                .defaultUnitId(2L)
                .ownerUserId(1L)
                .build();

        productService.createProduct(dto);

        verify(productRepository, times(1)).save(argThat(product ->
                product.getName().equals("Milk") &&
                        product.getDefaultUnit() == unit &&
                        product.getOwnerUser() == owner
        ));
    }

    @Test
    void createProduct_withNotValidUserOwner_throwsException() {
        Unit unit = Unit.builder().id(2L).name("liter").build();

        when(unitRepository.findById(2L)).thenReturn(Optional.of(unit));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ProductDto dto = ProductDto.builder()
                .name("Milk")
                .defaultUnitId(2L)
                .ownerUserId(1L)
                .build();

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> productService.createProduct(dto));
        assertEquals("User with email/id = 1 not found", ex.getMessage());
    }


    @Test
    @Disabled
    void addProductToRecipe_existingProductAndRecipe_addsIngredient() {
        Unit unit = Unit.builder().id(1L).name("kg").build();
        Product product = Product.builder().id(5L).name("Carrot").defaultUnit(unit).build();
        Recipe recipe = Recipe.builder().id(2L).ingredients(new ArrayList<>()).build();

        when(productRepository.findById(5L)).thenReturn(Optional.of(product));
        when(recipeRepository.findById(2L)).thenReturn(Optional.of(recipe));

        //productService.addProductToRecipe(5L, 2L);

        assertTrue(recipe.getIngredients().contains(product));
        verify(recipeRepository, times(1)).save(recipe);
    }

    @Test
    void getProductById_nonExistingProduct_throwsException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> productService.getProductById(99L));
        assertEquals("Product with email/id = 99 not found", ex.getMessage());
    }
}
