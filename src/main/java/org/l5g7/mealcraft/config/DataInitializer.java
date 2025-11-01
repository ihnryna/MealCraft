package org.l5g7.mealcraft.config;

import org.l5g7.mealcraft.app.user.PasswordHasher;
import org.l5g7.mealcraft.app.user.User;
import org.l5g7.mealcraft.app.user.UserRepository;
import org.l5g7.mealcraft.enums.Role;
import org.l5g7.mealcraft.logging.LogUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                PasswordHasher encoder = new PasswordHasher();

                User admin = User.builder()
                        .username("admin")
                        .email("admin@mealcraft.org")
                        .password(encoder.hashPassword("admin123"))
                        .role(Role.ADMIN)
                        .avatarUrl(null)
                        .build();

                User user = User.builder()
                        .username("user")
                        .email("user@mealcraft.org")
                        .password(encoder.hashPassword("user123"))
                        .role(Role.USER)
                        .avatarUrl(null)
                        .build();

                userRepository.save(admin);
                userRepository.save(user);

                LogUtils.logInfo("Default users created: admin & user");
            }
        };
    }
}