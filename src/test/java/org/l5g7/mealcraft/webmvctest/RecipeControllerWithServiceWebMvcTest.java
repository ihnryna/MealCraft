//package org.l5g7.mealcraft.webmvctest;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.l5g7.mealcraft.app.auth.security.JwtCookieFilter;
//import org.l5g7.mealcraft.app.products.Product;
//import org.l5g7.mealcraft.app.products.ProductRepository;
//import org.l5g7.mealcraft.app.recipeingredient.RecipeIngredient;
//import org.l5g7.mealcraft.app.recipeingredient.RecipeIngredientDto;
//import org.l5g7.mealcraft.app.recipes.*;
//import org.l5g7.mealcraft.app.units.Unit;
//import org.l5g7.mealcraft.app.units.UnitRepository;
//import org.l5g7.mealcraft.app.user.CurrentUserProviderImpl;
//import org.l5g7.mealcraft.app.user.User;
//import org.l5g7.mealcraft.app.user.UserRepository;
//import org.l5g7.mealcraft.mealcraftstarterexternalrecipes.RecipeProvider;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.FilterType;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Optional;
//
//import static org.hamcrest.Matchers.nullValue;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//
//@WebMvcTest(
//        controllers = RecipeController.class,
//        excludeFilters = @ComponentScan.Filter(
//                type = FilterType.ASSIGNABLE_TYPE,
//                classes = {JwtCookieFilter.class }
//        )
//)
//@Import({RecipeServiceImpl.class, CurrentUserProviderImpl.class})
//class RecipeControllerWithServiceWebMvcTest {
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private ObjectMapper objectMapper;
//    @MockitoBean
//    private ProductRepository productRepository;
//    @MockitoBean
//    private UserRepository userRepository;
//    @MockitoBean
//    private RecipeProvider recipeProvider;
//    @MockitoBean
//    private RecipeRepository recipeRepository;
//    @MockitoBean
//    private UnitRepository unitRepository;
//    @MockitoBean
//    private org.l5g7.mealcraft.app.units.UnitService unitService;
//    @MockitoBean
//    private org.l5g7.mealcraft.app.products.ProductService productService;
//
//    @Test
//    @WithMockUser(username = "user")
//    void getAllRecipes_returns200_andBody() throws Exception {
//        List<Recipe> entities = new ArrayList<>();
//        User owner = User.builder()
//                .id(1L)
//                .username("user")
//                .email("user@example.com")
//                .password("password")
//                .build();
//
//        Recipe baseRecipe = Recipe.builder()
//                .id(2L)
//                .name("Base Soup")
//                .createdAt(new Date())
//                .ownerUser(owner)
//                .ingredients(List.of())
//                .build();
//
//        Unit unit2 = Unit.builder()
//                .name("pc")
//                .build();
//
//        Product product1 = Product.builder()
//                .id(1L)
//                .name("Beetroot")
//                .defaultUnit(unit2)
//                .build();
//
//        Unit unit1 = Unit.builder()
//                .id(1L)
//                .name("liter")
//                .build();
//
//        Product product2 = Product.builder()
//                .id(2L)
//                .name("Water")
//                .defaultUnit(unit1)
//                .build();
//
//        Recipe recipe1 = Recipe.builder()
//                .id(1L)
//                .name("Borshch")
//                .createdAt(new Date())
//                .ownerUser(owner)
//                .baseRecipe(baseRecipe)
//                .imageUrl("https://example.com/borshch.jpg")
//                .build();
//
//        RecipeIngredient recipeIngredient11 = RecipeIngredient.builder()
//                .product(product1)
//                .recipe(recipe1)
//                .amount(1d)
//                .build();
//
//        RecipeIngredient recipeIngredient12 = RecipeIngredient.builder()
//                .product(product2)
//                .recipe(recipe1)
//                .amount(2.3d)
//                .build();
//
//        recipe1.setIngredients(List.of(recipeIngredient11, recipeIngredient12));
//
//        Recipe recipe2 = Recipe.builder()
//                .id(2L)
//                .name("Stewed beetroot")
//                .createdAt(new Date())
//                .ownerUser(null)
//                .baseRecipe(null)
//                .imageUrl(null)
//                .build();
//
//        RecipeIngredient recipeIngredient21 = RecipeIngredient.builder()
//                .product(product1)
//                .recipe(recipe2)
//                .amount(3d)
//                .build();
//
//        recipe2.setIngredients(List.of(recipeIngredient21));
//
//        entities.add(recipe1);
//        entities.add(recipe2);
//        when(recipeRepository.findAll()).thenReturn(entities);
//        when(recipeRepository.findAllByOwnerUserIsNullOrOwnerUser_Id(1L)).thenReturn(entities);
//        when(userRepository.findByUsername("user")).thenReturn(Optional.of(owner));
//
//        mockMvc.perform(get("/recipes"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1))
//                .andExpect(jsonPath("$[0].name").value("Borshch"))
//                .andExpect(jsonPath("$[0].ownerUserId").value(1))
//                .andExpect(jsonPath("$[0].baseRecipeId").value(2))
//                .andExpect(jsonPath("$[0].ingredients[0].productId").value(1))
//                .andExpect(jsonPath("$[0].ingredients[0].productName").value("Beetroot"))
//                .andExpect(jsonPath("$[0].ingredients[0].amount").value(1.0))
//                .andExpect(jsonPath("$[0].ingredients[1].productId").value(2))
//                .andExpect(jsonPath("$[0].ingredients[1].productName").value("Water"))
//                .andExpect(jsonPath("$[0].ingredients[1].amount").value(2.3))
//                .andExpect(jsonPath("$[0].imageUrl").value("https://example.com/borshch.jpg"))
//                .andExpect(jsonPath("$[1].id").value(2))
//                .andExpect(jsonPath("$[1].name").value("Stewed beetroot"))
//                .andExpect(jsonPath("$[1].ownerUserId").value(nullValue()))
//                .andExpect(jsonPath("$[1].baseRecipeId").value(nullValue()))
//                .andExpect(jsonPath("$[1].ingredients[0].productId").value(1))
//                .andExpect(jsonPath("$[1].ingredients[0].productName").value("Beetroot"))
//                .andExpect(jsonPath("$[1].ingredients[0].amount").value(3.0))                .andExpect(jsonPath("$[1].imageUrl").value(nullValue()));
//    }
//
//    @Test
//    @WithMockUser(username = "user")
//    void postRecipe_returns200() throws Exception {
//
//        User owner = User.builder()
//                .id(1L)
//                .username("user")
//                .email("user@example.com")
//                .password("password")
//                .build();
//
//        Recipe baseRecipe = Recipe.builder()
//                .id(2L)
//                .name("Base Soup")
//                .createdAt(new Date())
//                .ownerUser(owner)
//                .ingredients(List.of())
//                .build();
//
//        Unit unit2 = Unit.builder()
//                .id(2L)
//                .name("pc")
//                .build();
//
//        Product product1 = Product.builder()
//                .id(1L)
//                .name("Beetroot")
//                .defaultUnit(unit2)
//                .build();
//
//        Unit unit1 = Unit.builder()
//                .id(1L)
//                .name("liter")
//                .build();
//
//        Product product2 = Product.builder()
//                .id(2L)
//                .name("Water")
//                .defaultUnit(unit1)
//                .build();
//
//        RecipeIngredientDto recipeIngredientDto1 = RecipeIngredientDto.builder().productId(1L).amount(2d).build();
//        RecipeIngredientDto recipeIngredientDto2 = RecipeIngredientDto.builder().productId(2L).amount(3d).build();
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
//        when(unitRepository.findById(1L)).thenReturn(Optional.of(unit1));
//        when(unitRepository.findById(2L)).thenReturn(Optional.of(unit2));
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
//        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
//        when(recipeRepository.findById(2L)).thenReturn(Optional.ofNullable(baseRecipe));
//        when(productRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(product1, product2));
//        when(userRepository.findByUsername("user")).thenReturn(Optional.of(owner));
//
//        RecipeDto recipeDto = RecipeDto.builder()
//                .name("Stewed beetroot")
//                .ownerUserId(1L)
//                .baseRecipeId(2L)
//                .imageUrl("https://example.com/stewed-beetroot.jpg")
//                .build();
//
//        recipeDto.setIngredients(List.of(recipeIngredientDto1, recipeIngredientDto2));
//
//        mockMvc.perform(post("/recipes")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(recipeDto)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser(username = "user")
//    void postRecipeWithNullFields_returns200() throws Exception {
//        User owner = User.builder()
//                .id(1L)
//                .username("user")
//                .email("user@example.com")
//                .password("password")
//                .build();
//
//        Unit unit2 = Unit.builder()
//                .id(2L)
//                .name("pc")
//                .build();
//
//        Product product1 = Product.builder()
//                .id(1L)
//                .name("Beetroot")
//                .defaultUnit(unit2)
//                .build();
//
//        Unit unit1 = Unit.builder()
//                .id(1L)
//                .name("liter")
//                .build();
//
//        Product product2 = Product.builder()
//                .id(2L)
//                .name("Water")
//                .defaultUnit(unit1)
//                .build();
//
//        when(unitRepository.findById(1L)).thenReturn(Optional.of(unit1));
//        when(unitRepository.findById(2L)).thenReturn(Optional.of(unit2));
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
//        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
//        when(productRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(product1, product2));
//        when(userRepository.findByUsername("user")).thenReturn(Optional.of(owner));
//
//
//        RecipeDto recipeDto = RecipeDto.builder()
//                .name("Stewed beetroot")
//                .ingredients(List.of())
//                .build();
//
//        RecipeIngredientDto recipeIngredientDto = RecipeIngredientDto.builder().productId(1L).amount(2d).build();
//        recipeDto.setIngredients(List.of(recipeIngredientDto));
//
//        mockMvc.perform(post("/recipes")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(recipeDto)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser(username = "user")
//    void postRecipeWithoutName_returns400() throws Exception {
//
//        Unit unit2 = Unit.builder()
//                .id(2L)
//                .name("pc")
//                .build();
//
//        Product product1 = Product.builder()
//                .id(1L)
//                .name("Beetroot")
//                .defaultUnit(unit2)
//                .build();
//
//        when(unitRepository.findById(2L)).thenReturn(Optional.of(unit2));
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
//        when(productRepository.findAllById(List.of(1L))).thenReturn(List.of(product1));
//
//        RecipeDto recipeDto = RecipeDto.builder()
//                .build();
//
//        mockMvc.perform(post("/recipes")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(recipeDto)))
//                .andExpect(status().isBadRequest());
//    }
//
//}
