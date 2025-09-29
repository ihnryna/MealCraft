package org.l5g7.mealcraft.config;

import org.l5g7.mealcraft.service.AvatarService;
import org.l5g7.mealcraft.service.AvatarServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public AvatarService avatarService() {
        return new AvatarServiceImpl();
    }
}
