package org.l5g7.mealcraft.unittest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.l5g7.mealcraft.app.products.Product;
import org.l5g7.mealcraft.app.products.ProductDto;
import org.l5g7.mealcraft.app.products.ProductRepository;
import org.l5g7.mealcraft.app.products.ProductServiceImpl;
import org.l5g7.mealcraft.app.recipeingredient.RecipeIngredient;
import org.l5g7.mealcraft.app.recipes.Recipe;
import org.l5g7.mealcraft.app.recipes.RecipeRepository;
import org.l5g7.mealcraft.app.units.Entity.Unit;
import org.l5g7.mealcraft.app.units.interfaces.UnitRepository;
import org.l5g7.mealcraft.app.user.CurrentUserProvider;
import org.l5g7.mealcraft.app.user.User;
import org.l5g7.mealcraft.app.user.UserRepository;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceUnitTests {

    private ProductRepository productRepository;
    private UnitRepository unitRepository;
    private UserRepository userRepository;
    private RecipeRepository recipeRepository;
    private ProductServiceImpl productService;
    private CurrentUserProvider currentUserProvider;
    private User owner;
    private User anotherUser;
    private Unit unitOne;
    private Product productOne;


    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        unitRepository = mock(UnitRepository.class);
        userRepository = mock(UserRepository.class);
        recipeRepository = mock(RecipeRepository.class);
        currentUserProvider = mock(CurrentUserProvider.class);

        productService = new ProductServiceImpl(
                productRepository,
                unitRepository,
                recipeRepository,
                currentUserProvider
        );
        owner = User.builder()
                .id(1L)
                .username("user")
                .email("user@example.com")
                .password("password")
                .build();

        unitOne = Unit.builder().id(10L).name("kg").build();

        productOne = Product.builder()
                .id(100L)
                .name("Milk")
                .defaultUnit(unitOne)
                .ownerUser(owner)
                .build();

        anotherUser = User.builder().id(2L).build();

    }

    @Test
    void getAllProducts_byAdmin_returnsDtosList() {
        Unit unit = Unit.builder().id(1L).name("kg").build();
        Unit unit2 = Unit.builder().id(2L).name("pc").build();

        Product product = Product.builder()
                .id(10L)
                .name("Super Tomato")
                .defaultUnit(unit)
                .ownerUser(owner)
                .build();

        Product product2 = Product.builder()
                .id(11L)
                .name("Tomato")
                .defaultUnit(unit2)
                .ownerUser(null)
                .build();

        when(productRepository.findAllByOwnerUserIsNull()).thenReturn(List.of(product, product2, productOne));
        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(null);

        List<ProductDto> dtos = productService.getAllProducts();

        assertEquals(10L, dtos.get(0).getId());
        assertEquals("Super Tomato", dtos.get(0).getName());
        assertEquals(1L, dtos.get(0).getDefaultUnitId());
        assertEquals(1L, dtos.get(0).getOwnerUserId());
        assertEquals(11L, dtos.get(1).getId());
        assertEquals("Tomato", dtos.get(1).getName());
        assertEquals(2L, dtos.get(1).getDefaultUnitId());
        assertNull(dtos.get(1).getOwnerUserId());
        assertEquals(100L, dtos.get(2).getId());
        assertEquals("Milk", dtos.get(2).getName());
        assertEquals(10L, dtos.get(2).getDefaultUnitId());
        assertEquals(1L, dtos.get(2).getOwnerUserId());
    }

    @Test
    void getAllProducts_byUser_returnsValidDtosList() {
        Unit unit = Unit.builder().id(1L).name("kg").build();
        Unit unit2 = Unit.builder().id(2L).name("pc").build();

        Product product = Product.builder()
                .id(10L)
                .name("Super Tomato")
                .defaultUnit(unit)
                .ownerUser(owner)
                .build();

        Product product2 = Product.builder()
                .id(11L)
                .name("Tomato")
                .defaultUnit(unit2)
                .ownerUser(null)
                .build();

        when(productRepository.findAllByOwnerUserIsNull()).thenReturn(List.of(product, product2, productOne));
        when(productRepository.findAllByOwnerUserIsNullOrOwnerUser_Id(owner.getId())).thenReturn(List.of(product, productOne));
        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(owner);

        List<ProductDto> dtos = productService.getAllProducts();

        assertEquals(10L, dtos.get(0).getId());
        assertEquals("Super Tomato", dtos.get(0).getName());
        assertEquals(1L, dtos.get(0).getDefaultUnitId());
        assertEquals(1L, dtos.get(0).getOwnerUserId());
        assertEquals(100L, dtos.get(1).getId());
        assertEquals("Milk", dtos.get(1).getName());
        assertEquals(10L, dtos.get(1).getDefaultUnitId());
        assertEquals(1L, dtos.get(1).getOwnerUserId());
    }

    @Test
    void getProductById_existingProduct_returnsDto() {
        Unit unit = Unit.builder().id(1L).name("kg").build();

        Product product = Product.builder()
                .id(10L)
                .name("Tomato")
                .defaultUnit(unit)
                .ownerUser(owner)
                .build();

        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(owner);

        ProductDto dto = productService.getProductById(10L);

        assertEquals(10L, dto.getId());
        assertEquals("Tomato", dto.getName());
        assertEquals(1L, dto.getDefaultUnitId());
        assertEquals(1L, dto.getOwnerUserId());
    }

    @Test
    void getPrivateProductById_byAnotherUser_throws() {
        Unit unit = Unit.builder().id(1L).name("kg").build();

        Product product = Product.builder()
                .id(10L)
                .name("Tomato")
                .defaultUnit(unit)
                .ownerUser(owner)
                .build();

        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(anotherUser);

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> productService.getProductById(10L));
        assertEquals("Product not found", ex.getMessage());
    }

    @Test
    void createProduct_withValidDto_savesProduct() {
        Unit unit = Unit.builder().id(2L).name("liter").build();

        when(unitRepository.findById(2L)).thenReturn(Optional.of(unit));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(owner);

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
    void createProduct_withNotValidUnit_throws() {

        when(unitRepository.findById(2L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(owner);

        ProductDto dto = ProductDto.builder()
                .name("Milk")
                .defaultUnitId(2L)
                .ownerUserId(1L)
                .build();

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> productService.createProduct(dto));
        assertEquals("Unit not found", ex.getMessage());
    }

    @Test
    void createProduct_byAdmin_savesProduct() {
        Unit unit = Unit.builder().id(2L).name("liter").build();

        when(unitRepository.findById(2L)).thenReturn(Optional.of(unit));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(null);

        ProductDto dto = ProductDto.builder()
                .name("Milk")
                .defaultUnitId(2L)
                .ownerUserId(null)
                .build();

        productService.createProduct(dto);

        verify(productRepository, times(1)).save(argThat(product ->
                product.getName().equals("Milk") &&
                        product.getDefaultUnit() == unit &&
                        product.getOwnerUser() == null
        ));
    }

    @Test
    void addProductToRecipe_existingProductAndRecipe_addsIngredient() {
        Unit unit = Unit.builder().id(1L).name("kg").build();
        Product product = Product.builder().id(5L).name("Carrot").defaultUnit(unit).build();
        Recipe recipe = Recipe.builder().id(2L).ingredients(new ArrayList<>()).build();

        when(productRepository.findById(5L)).thenReturn(Optional.of(product));
        when(recipeRepository.findById(2L)).thenReturn(Optional.of(recipe));
        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(owner);

        productService.addProductToRecipe(2L, 5L,2d);

        RecipeIngredient recipeIngredient = RecipeIngredient.builder()
                .product(product)
                .recipe(recipe)
                .amount(2d)
                .build();

        assertTrue(recipe.getIngredients().contains(recipeIngredient));
    }

    @Test
    void addProductToRecipe_notExistingProduct_throws() {
        Recipe recipe = Recipe.builder().id(2L).ingredients(new ArrayList<>()).build();

        when(productRepository.findById(5L)).thenReturn(Optional.empty());
        when(recipeRepository.findById(2L)).thenReturn(Optional.of(recipe));
        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(owner);

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> productService.addProductToRecipe(2L, 5L,2d));
        assertEquals("Product not found", ex.getMessage());
    }

    @Test
    void addProductToRecipe_notExistingRecipe_throws() {
        Unit unit = Unit.builder().id(1L).name("kg").build();
        Product product = Product.builder().id(5L).name("Carrot").defaultUnit(unit).build();

        when(productRepository.findById(5L)).thenReturn(Optional.of(product));
        when(recipeRepository.findById(2L)).thenReturn(Optional.empty());
        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(owner);

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> productService.addProductToRecipe(2L, 5L,2d));
        assertEquals("Recipe not found", ex.getMessage());
    }

    @Test
    void getProductById_nonExistingProduct_throwsException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(owner);

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> productService.getProductById(99L));
        assertEquals("Product not found", ex.getMessage());
    }

    @Test
    void updateProduct_success_whenOwnerUpdates() {
        ProductDto dto = ProductDto.builder()
                .name("New name")
                .imageUrl("img")
                .defaultUnitId(10L)
                .build();

        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(owner);
        when(productRepository.findById(100L)).thenReturn(Optional.of(productOne));
        when(unitRepository.findById(10L)).thenReturn(Optional.of(unitOne));

        productService.updateProduct(100L, dto);

        verify(productRepository).save(productOne);
    }

    @Test
    void updateProduct_whenProductNotFound_throws() {
        when(productRepository.findById(100L)).thenReturn(Optional.empty());
        ProductDto dto = ProductDto.builder().defaultUnitId(10L).build();

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> productService.updateProduct(100L,dto));
        assertEquals("Product not found", ex.getMessage());
    }

    @Test
    void updateProduct_fails_whenUnitNotFound() {
        ProductDto dto = ProductDto.builder().defaultUnitId(10L).build();

        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(owner);
        when(productRepository.findById(100L)).thenReturn(Optional.of(productOne));
        when(unitRepository.findById(10L)).thenReturn(Optional.empty());

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> productService.updateProduct(100L, dto));
        assertEquals("Unit not found", ex.getMessage());
    }

    @Test
    void updateProduct_adminCanUpdatePublicProduct() {
        productOne.setOwnerUser(null);

        ProductDto dto = ProductDto.builder().defaultUnitId(10L).build();

        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(null);
        when(productRepository.findById(100L)).thenReturn(Optional.of(productOne));
        when(unitRepository.findById(10L)).thenReturn(Optional.of(unitOne));

        productService.updateProduct(100L, dto);

        verify(productRepository).save(productOne);
    }

    @Test
    void updateProduct_adminCantUpdatePrivateProduct() {
        productOne.setOwnerUser(owner);

        ProductDto dto = ProductDto.builder().defaultUnitId(10L).build();

        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(null);
        when(productRepository.findById(100L)).thenReturn(Optional.of(productOne));
        when(unitRepository.findById(10L)).thenReturn(Optional.of(unitOne));

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> productService.updateProduct(100L, dto));
        assertEquals("Product not found", ex.getMessage());
    }

    @Test
    void updateProduct_userCantUpdatePrivateProductOfAnotherUser() {
        productOne.setOwnerUser(anotherUser);

        ProductDto dto = ProductDto.builder().defaultUnitId(10L).build();

        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(null);
        when(productRepository.findById(100L)).thenReturn(Optional.of(productOne));
        when(unitRepository.findById(10L)).thenReturn(Optional.of(unitOne));

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> productService.updateProduct(100L, dto));
        assertEquals("Product not found", ex.getMessage());
    }

    @Test
    void updateProduct_userCantUpdatePublicProduct() {
        productOne.setOwnerUser(null);

        ProductDto dto = ProductDto.builder().defaultUnitId(10L).build();

        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(owner);
        when(productRepository.findById(100L)).thenReturn(Optional.of(productOne));
        when(unitRepository.findById(10L)).thenReturn(Optional.of(unitOne));

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> productService.updateProduct(100L, dto));
        assertEquals("Product not found", ex.getMessage());
    }

    @Test
    void patchProduct_success_ownerEdits() {
        ProductDto dto = ProductDto.builder()
                .name("Patch name")
                .defaultUnitId(10L)
                .build();

        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(owner);
        when(productRepository.findById(100L)).thenReturn(Optional.of(productOne));
        when(unitRepository.findById(10L)).thenReturn(Optional.of(unitOne));

        productService.patchProduct(100L, dto);

        verify(productRepository).save(productOne);
    }

    @Test
    void deleteProduct_success_ownerDeletes() {
        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(owner);
        when(productRepository.findById(100L)).thenReturn(Optional.of(productOne));
        when(recipeRepository.findAllByIngredientsProduct(productOne)).thenReturn(List.of());

        productService.deleteProductById(100L);

        verify(productRepository).delete(productOne);
    }

    @Test
    void deleteProduct_adminCanDeletePublicProduct() {
        productOne.setOwnerUser(null);

        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(null);
        when(productRepository.findById(100L)).thenReturn(Optional.of(productOne));
        when(recipeRepository.findAllByIngredientsProduct(productOne)).thenReturn(List.of());

        productService.deleteProductById(100L);

        verify(productRepository).delete(productOne);
    }

    @Test
    void deleteProduct_fails_whenUsedInRecipes() {
        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(owner);
        when(productRepository.findById(100L)).thenReturn(Optional.of(productOne));
        when(recipeRepository.findAllByIngredientsProduct(productOne))
                .thenReturn(List.of(new Recipe()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.deleteProductById(100L));
        assertEquals("Cannot delete product because it is used in some recipes", ex.getMessage());
    }

    @Test
    void deleteProduct_fails_nonOwnerDeletesPrivateProduct() {
        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(anotherUser);
        when(productRepository.findById(100L)).thenReturn(Optional.of(productOne));


        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> productService.deleteProductById(100L));
        assertEquals("Product not found", ex.getMessage());
    }

    @Test
    void searchProducts_adminGetsOnlyPublic() {
        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(null);
        when(productRepository.findAllByOwnerUserIsNullAndNameStartingWithIgnoreCase("Mi"))
                .thenReturn(List.of(productOne));

        var result = productService.searchProductsByPrefix("Mi");

        verify(productRepository).findAllByOwnerUserIsNullAndNameStartingWithIgnoreCase("Mi");
        assert(result.size() == 1);
    }

    @Test
    void searchProducts_userGetsPublicAndOwn() {
        when(currentUserProvider.getCurrentUserOrNullIfAdmin()).thenReturn(owner);
        when(productRepository
                .findAllByOwnerUserIsNullOrOwnerUser_IdAndNameStartingWithIgnoreCase(1L, "Mi"))
                .thenReturn(List.of(productOne));

        var result = productService.searchProductsByPrefix("Mi");

        verify(productRepository)
                .findAllByOwnerUserIsNullOrOwnerUser_IdAndNameStartingWithIgnoreCase(1L, "Mi");
        assert(result.size() == 1);
    }
}
