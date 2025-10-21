package org.l5g7.mealcraft;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "MealCraft API",
                version = "1.0.0",
                description = "Це API для керування рецептами, інгредієнтами та покупками."
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Локальний сервер"),
        }

)
@SpringBootApplication
public class MealCraftApplication {

    public static void main(String[] args) {
        SpringApplication.run(MealCraftApplication.class, args);
    }

}
