package org.l5g7.mealcraft.dao;

import org.l5g7.mealcraft.entity.User;
import org.l5g7.mealcraft.enums.Role;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    User user1 = new User(1L, "Vika", "vika@gmail.com", Role.USER, "123", "");
    User user2 = new User(2L, "Kate", "kate@gmail.com", Role.USER, "456", "");
    User user3 = new User(3L, "Angelina", "angelina@gmail.com", Role.ADMIN, "789", "");
    User user4 = new User(4L, "Vika2", "vika2@gmail.com", Role.USER, "hashed_123", "");
    List<User> users = new ArrayList<User>(List.of(user1,user2,user3));


    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public Optional<User> findById(Long id) {
        return users
                .stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public void create(User user) {
        users.add(user);
    }

    @Override
    public boolean update(User user) {
        for (User u : users) {
            if (u.getId().equals(user.getId())) {
                u.setUsername(user.getUsername());
                u.setEmail(user.getEmail());
                u.setRole(user.getRole());
                u.setPassword(user.getPassword());
                u.setAvatarUrl(user.getAvatarUrl());
                return true;
            }
        }
        return false;
    }



    @Override
    public boolean deleteById(Long id) {
        return users.removeIf(user -> user.getId().equals(id));
    }
}



