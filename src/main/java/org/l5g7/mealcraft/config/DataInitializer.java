package org.l5g7.mealcraft.config;

import org.l5g7.mealcraft.app.products.Product;
import org.l5g7.mealcraft.app.products.ProductRepository;
import org.l5g7.mealcraft.app.shoppingitem.ShoppingItem;
import org.l5g7.mealcraft.app.shoppingitem.ShoppingItemRepository;
import org.l5g7.mealcraft.app.units.Entity.Unit;
import org.l5g7.mealcraft.app.units.interfaces.UnitRepository;
import org.l5g7.mealcraft.app.user.PasswordHasher;
import org.l5g7.mealcraft.app.user.User;
import org.l5g7.mealcraft.app.user.UserRepository;
import org.l5g7.mealcraft.enums.Role;
import org.l5g7.mealcraft.logging.LogUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Calendar;
import java.util.Date;

@Configuration
@Profile("dev")
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, UnitRepository unitRepository, ProductRepository productRepository, ShoppingItemRepository shoppingItemRepository) {
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
                LogUtils.logInfo(user.getId()+"");


                Unit unit1 = Unit.builder()
                        .name("liter")
                        .build();

                Product product1 = Product.builder()
                        .name("Milk")
                        .imageUrl(null)
                        .defaultUnit(unit1)
                        .createdAt(yesterday)
                        .build();

                ShoppingItem shoppingItem1 = ShoppingItem.builder()
                        .userOwner(user)
                        .product(product1)
                        .requiredQty(3)
                        .status(false)
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

                ShoppingItem shoppingItem2 = ShoppingItem.builder()
                        .userOwner(user)
                        .product(product2)
                        .requiredQty(10)
                        .status(false)
                        .build();

                Product product3 = Product.builder()
                        .name("Bread")
                        .imageUrl(null)
                        .defaultUnit(unit2)
                        .createdAt(yesterday)
                        .build();

                ShoppingItem shoppingItem3 = ShoppingItem.builder()
                        .userOwner(user)
                        .product(product3)
                        .requiredQty(1)
                        .status(false)
                        .build();

                unitRepository.save(unit1);
                unitRepository.save(unit2);
                productRepository.save(product1);
                productRepository.save(product2);
                productRepository.save(product3);
                shoppingItemRepository.save(shoppingItem1);
                shoppingItemRepository.save(shoppingItem2);
                shoppingItemRepository.save(shoppingItem3);
            }
        };
    }
}
