package org.l5g7.mealcraft.controller;

import jakarta.validation.Valid;
import org.l5g7.mealcraft.entity.User;
import org.l5g7.mealcraft.enums.Role;
import org.l5g7.mealcraft.exception.EntityAlreadyExistsException;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    User user1 = new User(1L, "kate", "kate@gmail.com", Role.USER, "456", "");
    User user2 = new User(2L, "maria", "maria@gmail.com", Role.USER, "789", "");
    List<User> users = new ArrayList<>(List.of(user1, user2));

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        throw new EntityDoesNotExistException("User", id);
    }

    @PostMapping
    public void createUser(@RequestBody @Valid User user) {
        long idUserToCreate = user.getId();
        for (User user1 : users) {
            if (user1.getId().equals(idUserToCreate)) {
                throw new EntityAlreadyExistsException("User", idUserToCreate);
            }
        }
        users.add(user);
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable Long id, @RequestBody @Valid User user) {
        for (User user1 : users) {
            if (user1.getId().equals(id)) {
                user1.setUsername(user.getUsername());
                user1.setEmail(user.getEmail());
                user1.setRole(user.getRole());
                user1.setPassword(user.getPassword());
                user1.setAvatarUrl(user.getAvatarUrl());
                return;
            }
        }
        throw new EntityDoesNotExistException("User", id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        boolean removed = users.removeIf(u -> u.getId().equals(id));
        if (!removed) throw new EntityDoesNotExistException("User", id);
    }

    @PatchMapping("/{id}")
    public void patchUser(@PathVariable Long id, @RequestBody User user) {
        for (User user1 : users) {
            if (user1.getId().equals(id)) {
                if (user.getUsername() != null) {
                    user1.setUsername(user.getUsername());
                }
                if (user.getEmail() != null) {
                    user1.setEmail(user.getEmail());
                }
                if (user.getRole() != null) {
                    user1.setRole(user.getRole());
                }
                if (user.getPassword() != null) {
                    user1.setPassword(user.getPassword());
                }
                if (user.getAvatarUrl() != null) {
                    user1.setAvatarUrl(user.getAvatarUrl());
                }
                return;
            }
        }
        throw new EntityDoesNotExistException("User", id);
    }

}
