package org.l5g7.mealcraft.config;

import org.l5g7.mealcraft.app.mealplan.MealPlan;
import org.l5g7.mealcraft.app.mealplan.MealPlanRepository;
import org.l5g7.mealcraft.app.products.Product;
import org.l5g7.mealcraft.app.products.ProductRepository;
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
                                   ProductRepository productRepository, RecipeRepository recipeRepository, MealPlanRepository mealPlanRepository, ShoppingItemRepository shoppingItemRepository) {
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
                        .email("ira@mealcraft.org")
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

                unitRepository.save(unit1);
                unitRepository.save(unit2);
                productRepository.save(product1);
                productRepository.save(product2);
                productRepository.save(product3);

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
                        .ingredients(List.of(product1, product2, product3))
                        .build();

                Recipe recipe2 = Recipe.builder()
                        .name("Stewed beetroot")
                        .createdAt(new Date())
                        .ownerUser(null)
                        .baseRecipe(null)
                        .imageUrl(null)
                        .ingredients(List.of(product1))
                        .build();

                LocalDate localPlanDate1 = LocalDate.of(2025, 11, 3);
                LocalDate localPlanDate2 = LocalDate.of(2025, 11, 5);
                Date planDate1 = Date.from(localPlanDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
                Date planDate2 = Date.from(localPlanDate2.atStartOfDay(ZoneId.systemDefault()).toInstant());
                recipeRepository.save(baseRecipe);
                recipeRepository.save(recipe1);
                recipeRepository.save(recipe2);

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

                for(Product product : mealPlan2.getRecipe().getIngredients()){
                    shoppingItemRepository.save(new ShoppingItem(null,mealPlan2.getUserOwner(),product,1,false,null));
                }
                for(Product product : mealPlan1.getRecipe().getIngredients()){
                    shoppingItemRepository.save(new ShoppingItem(null,mealPlan1.getUserOwner(),product,1,false,null));
                }
            }
        };
    }
}
