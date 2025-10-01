package org.l5g7.mealcraft.service;

import org.springframework.stereotype.Component;

@Component
public class PasswordHasher {

    public String hashPassword(String password) {
        //тут заюзаємо який алгоритм хешування
        return "hashed_" + password;
    }
}
