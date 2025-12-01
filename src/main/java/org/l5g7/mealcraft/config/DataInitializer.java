package org.l5g7.mealcraft.config;

import org.l5g7.mealcraft.app.mealplan.MealPlan;
import org.l5g7.mealcraft.app.mealplan.MealPlanRepository;
import org.l5g7.mealcraft.app.products.Product;
import org.l5g7.mealcraft.app.products.ProductRepository;
import org.l5g7.mealcraft.app.recipeingredient.RecipeIngredient;
import org.l5g7.mealcraft.app.recipeingredient.RecipeIngredientRepository;
import org.l5g7.mealcraft.app.recipes.Recipe;
import org.l5g7.mealcraft.app.recipes.RecipeRepository;
import org.l5g7.mealcraft.app.shoppingitem.ShoppingItem;
import org.l5g7.mealcraft.app.shoppingitem.ShoppingItemRepository;
import org.l5g7.mealcraft.app.units.Entity.Unit;
import org.l5g7.mealcraft.app.units.interfaces.UnitRepository;
import org.l5g7.mealcraft.app.user.PasswordHasher;
import org.l5g7.mealcraft.app.user.User;
import org.l5g7.mealcraft.app.user.UserRepository;
import org.l5g7.mealcraft.enums.MealPlanColor;
import org.l5g7.mealcraft.enums.MealStatus;
import org.l5g7.mealcraft.enums.Role;
import org.l5g7.mealcraft.logging.LogUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Configuration
@Profile("dev")
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   UnitRepository unitRepository,
                                   ProductRepository productRepository,
                                   RecipeRepository recipeRepository,
                                   MealPlanRepository mealPlanRepository, ShoppingItemRepository shoppingItemRepository, RecipeIngredientRepository recipeIngredientRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                PasswordHasher encoder = new PasswordHasher();

                Date now = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(now);
                cal.add(Calendar.DATE, -1);
                Date yesterday = cal.getTime();

                User admin = User.builder()
                        .username("admin")
                        .email("admin@mealcraft.org")
                        .password(encoder.hashPassword("admin123"))
                        .role(Role.ADMIN)
                        .avatarUrl("https://images.unsplash.com/photo-1495745966610-2a67f2297e5e?ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8cGhvdG9ncmFwaGVyfGVufDB8fDB8fHww&fm=jpg&q=60&w=3000")
                        .createdAt(now)
                        .build();

                User user = User.builder()
                        .username("user")
                        .email("user@mealcraft.org")
                        .password(encoder.hashPassword("user123"))
                        .role(Role.USER)
                        .avatarUrl(null)
                        .createdAt(now)
                        .build();

                User premiumUser = User.builder()
                        .username("ira")
                        .email("ira@mealcraft.o rg")
                        .password(encoder.hashPassword("ira123"))
                        .role(Role.PREMIUM_USER)
                        .avatarUrl(null)
                        .createdAt(yesterday)
                        .build();

                userRepository.save(admin);
                userRepository.save(user);
                userRepository.save(premiumUser);

                LogUtils.logInfo("Default users created: admin & user");
                LogUtils.logInfo(user.getId() + "");

                Unit unit1 = Unit.builder()
                        .name("liter")
                        .build();

                Product product1 = Product.builder()
                        .name("Milk")
                        .imageUrl(null)
                        .defaultUnit(unit1)
                        .createdAt(yesterday)
                        .build();

                Unit unit2 = Unit.builder()
                        .name("pc")
                        .build();

                Product product2 = Product.builder()
                        .name("Egg")
                        .imageUrl(null)
                        .defaultUnit(unit2)
                        .createdAt(yesterday)
                        .build();

                Product product3 = Product.builder()
                        .name("Bread")
                        .imageUrl(null)
                        .defaultUnit(unit2)
                        .createdAt(yesterday)
                        .build();

                Product product4 = Product.builder()
                        .name("Tomato")
                        .imageUrl(null)
                        .defaultUnit(unit2)
                        .createdAt(yesterday)
                        .build();

                Product product5 = Product.builder()
                        .name("Cheese")
                        .imageUrl(null)
                        .defaultUnit(unit2)
                        .createdAt(yesterday)
                        .build();

                Product product6 = Product.builder()
                        .name("Chicken")
                        .imageUrl(null)
                        .defaultUnit(unit2)
                        .createdAt(yesterday)
                        .build();

                Product product7 = Product.builder()
                        .name("Rice")
                        .imageUrl(null)
                        .defaultUnit(unit1)
                        .createdAt(yesterday)
                        .build();

                unitRepository.save(unit1);
                unitRepository.save(unit2);
                productRepository.save(product1);
                productRepository.save(product2);
                productRepository.save(product3);
                productRepository.save(product4);
                productRepository.save(product5);
                productRepository.save(product6);
                productRepository.save(product7);

                Product userOnlyProduct1 = Product.builder()
                        .name("User secret cheese")
                        .imageUrl(null)
                        .defaultUnit(unit2)
                        .ownerUser(user)
                        .createdAt(now)
                        .build();

                Product userOnlyProduct2 = Product.builder()
                        .name("Ira private butter")
                        .imageUrl(null)
                        .defaultUnit(unit2)
                        .ownerUser(premiumUser)
                        .createdAt(now)
                        .build();

                productRepository.save(userOnlyProduct1);
                productRepository.save(userOnlyProduct2);

                Recipe userRecipe1 = Recipe.builder()
                        .name("User's secret sandwich")
                        .ownerUser(user)
                        .imageUrl(null)
                        .baseRecipe(null)
                        .createdAt(now)
                        .build();

                Recipe userRecipe2 = Recipe.builder()
                        .name("Ira's private toast")
                        .ownerUser(premiumUser)
                        .imageUrl(null)
                        .baseRecipe(null)
                        .createdAt(now)
                        .build();

                recipeRepository.save(userRecipe1);
                recipeRepository.save(userRecipe2);

                Recipe baseRecipe = Recipe.builder()
                        .name("Base Soup")
                        .createdAt(new Date())
                        .ownerUser(null)
                        .ingredients(List.of())
                        .build();

                Recipe recipe1 = Recipe.builder()
                        .name("Borshch")
                        .createdAt(new Date())
                        .ownerUser(user)
                        .baseRecipe(baseRecipe)
                        .imageUrl("https://example.com/borshch.jpg")
                        .build();

                RecipeIngredient recipeIngredient1 = RecipeIngredient.builder()
                        .product(product1)
                        .recipe(recipe1)
                        .amount(1d)
                        .build();

                RecipeIngredient recipeIngredient2 = RecipeIngredient.builder()
                        .product(product2)
                        .recipe(recipe1)
                        .amount(1d)
                        .build();

                RecipeIngredient recipeIngredient3 = RecipeIngredient.builder()
                        .product(product3)
                        .recipe(recipe1)
                        .amount(1d)
                        .build();

                Recipe recipe2 = Recipe.builder()
                        .name("Stewed beetroot")
                        .createdAt(new Date())
                        .ownerUser(null)
                        .baseRecipe(null)
                        .imageUrl(null)
                        .build();

                RecipeIngredient recipeIngredient21 = RecipeIngredient.builder()
                        .product(product1)
                        .recipe(recipe2)
                        .amount(1d)
                        .build();

                recipe1.setIngredients(List.of(recipeIngredient1, recipeIngredient2, recipeIngredient3));
                recipe2.setIngredients(List.of(recipeIngredient21));

                // Recipe 3: Cheese Sandwich (public recipe with Bread + Cheese)
                Recipe recipe3 = Recipe.builder()
                        .name("Cheese Sandwich")
                        .createdAt(new Date())
                        .ownerUser(null) // Public recipe
                        .baseRecipe(null)
                        .imageUrl("https://example.com/cheese-sandwich.jpg")
                        .build();

                RecipeIngredient recipeIngredient31 = RecipeIngredient.builder()
                        .product(product3) // Bread
                        .recipe(recipe3)
                        .amount(2d)
                        .build();

                RecipeIngredient recipeIngredient32 = RecipeIngredient.builder()
                        .product(product5) // Cheese
                        .recipe(recipe3)
                        .amount(1d)
                        .build();

                recipe3.setIngredients(List.of(recipeIngredient31, recipeIngredient32));

                // Recipe 4: Tomato Omelet (public recipe with Egg + Tomato)
                Recipe recipe4 = Recipe.builder()
                        .name("Tomato Omelet")
                        .createdAt(new Date())
                        .ownerUser(null) // Public recipe
                        .baseRecipe(null)
                        .imageUrl("https://example.com/omelet.jpg")
                        .build();

                RecipeIngredient recipeIngredient41 = RecipeIngredient.builder()
                        .product(product2) // Egg
                        .recipe(recipe4)
                        .amount(3d)
                        .build();

                RecipeIngredient recipeIngredient42 = RecipeIngredient.builder()
                        .product(product4) // Tomato
                        .recipe(recipe4)
                        .amount(2d)
                        .build();

                recipe4.setIngredients(List.of(recipeIngredient41, recipeIngredient42));

                // Recipe 5: Chicken Rice Bowl (user's private recipe with Chicken + Rice)
                Recipe recipe5 = Recipe.builder()
                        .name("Chicken Rice Bowl")
                        .createdAt(new Date())
                        .ownerUser(user) // User's private recipe
                        .baseRecipe(null)
                        .imageUrl("https://example.com/chicken-rice.jpg")
                        .build();

                RecipeIngredient recipeIngredient51 = RecipeIngredient.builder()
                        .product(product6) // Chicken
                        .recipe(recipe5)
                        .amount(1d)
                        .build();

                RecipeIngredient recipeIngredient52 = RecipeIngredient.builder()
                        .product(product7) // Rice
                        .recipe(recipe5)
                        .amount(1d)
                        .build();

                recipe5.setIngredients(List.of(recipeIngredient51, recipeIngredient52));

                // Recipe 6: Ultimate Sandwich (premium user's recipe with multiple ingredients)
                Recipe recipe6 = Recipe.builder()
                        .name("Ultimate Sandwich")
                        .createdAt(new Date())
                        .ownerUser(premiumUser) // Premium user's recipe
                        .baseRecipe(null)
                        .imageUrl("https://example.com/ultimate-sandwich.jpg")
                        .build();

                RecipeIngredient recipeIngredient61 = RecipeIngredient.builder()
                        .product(product3) // Bread
                        .recipe(recipe6)
                        .amount(2d)
                        .build();

                RecipeIngredient recipeIngredient62 = RecipeIngredient.builder()
                        .product(product5) // Cheese
                        .recipe(recipe6)
                        .amount(2d)
                        .build();

                RecipeIngredient recipeIngredient63 = RecipeIngredient.builder()
                        .product(product4) // Tomato
                        .recipe(recipe6)
                        .amount(1d)
                        .build();

                RecipeIngredient recipeIngredient64 = RecipeIngredient.builder()
                        .product(product6) // Chicken
                        .recipe(recipe6)
                        .amount(1d)
                        .build();

                recipe6.setIngredients(List.of(recipeIngredient61, recipeIngredient62, recipeIngredient63, recipeIngredient64));

                LocalDate localPlanDate1 = LocalDate.of(2025, 12, 3);
                LocalDate localPlanDate2 = LocalDate.of(2025, 12, 5);
                Date planDate1 = Date.from(localPlanDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
                Date planDate2 = Date.from(localPlanDate2.atStartOfDay(ZoneId.systemDefault()).toInstant());

                recipeRepository.save(baseRecipe);
                recipeRepository.save(recipe1);
                recipeRepository.save(recipe2);
                recipeRepository.save(recipe3);
                recipeRepository.save(recipe4);
                recipeRepository.save(recipe5);
                recipeRepository.save(recipe6);


                MealPlan mealPlan1 = MealPlan.builder()
                        .userOwner(user)
                        .recipe(recipe1)
                        .planDate(planDate1)
                        .servings(3)
                        .status(MealStatus.PLANNED)
                        .color(MealPlanColor.BLUE)
                        .build();

                MealPlan mealPlan2 = MealPlan.builder()
                        .userOwner(user)
                        .recipe(recipe2)
                        .planDate(planDate2)
                        .servings(12)
                        .status(MealStatus.PLANNED)
                        .color(MealPlanColor.ORANGE)
                        .build();

                mealPlanRepository.save(mealPlan1);
                mealPlanRepository.save(mealPlan2);


                for(RecipeIngredient ingredient : mealPlan2.getRecipe().getIngredients()){
                    shoppingItemRepository.save(new ShoppingItem(null,mealPlan2.getUserOwner(),ingredient.getProduct(),ingredient.getAmount(),false,null));
                }
                for(RecipeIngredient ingredient : mealPlan1.getRecipe().getIngredients()){
                    shoppingItemRepository.save(new ShoppingItem(null,mealPlan1.getUserOwner(),ingredient.getProduct(),ingredient.getAmount(),false,null));
                }
            }
        };
    }
}
