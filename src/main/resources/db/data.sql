insert into users (avatar_url, email, password, role, username) values
                                                                         (null, 'vika@gmail.com','13', 'USER', 'Vika'),
                                                                         (null, 'alex@example.com', '123', 'USER', 'alex');

insert into notification (user_id, text, created_at) values
                                                         (1, 'Ласкаво просимо до MealCraft! 🎉', CURRENT_TIMESTAMP),
                                                         (2, 'Ваш рецепт успішно збережено.',    CURRENT_TIMESTAMP);
