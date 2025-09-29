package org.l5g7.mealcraft.controller;

import jakarta.validation.Valid;
import org.l5g7.mealcraft.entity.User;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.l5g7.mealcraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
       return userService.getUserById(id);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public void createUser(@RequestBody @Valid User user) {
        userService.createUser(user);
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable Long id, @RequestBody @Valid User user) {
        if(!id.equals(user.getId())) {
            throw new EntityDoesNotExistException("User", id);
        }
        userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }

    @PatchMapping("/{id}")
    public void patchUser(@PathVariable Long id, @RequestBody User user) {
        User existingUser = userService.getUserById(id);
        userService.updateUser(existingUser);
    }

}
