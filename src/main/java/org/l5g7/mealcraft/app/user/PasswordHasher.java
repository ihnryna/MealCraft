package org.l5g7.mealcraft.app.user;

import org.springframework.stereotype.Component;

@Component
public class PasswordHasher {

    public String hashPassword(String password) {
        //тут заюзаємо який алгоритм хешування
        return "hashed_" + password;
    }

    public boolean matches(String rawPassword, String hashedPassword) {
        return hashedPassword.equals(hashPassword(rawPassword));
    }
}
