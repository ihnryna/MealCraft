package org.l5g7.mealcraft.app.user;

import jakarta.validation.Valid;
import org.l5g7.mealcraft.mealcraftexternalrecipesstarter.ExternalRecipe;
import org.l5g7.mealcraft.mealcraftexternalrecipesstarter.ExternalRecipeService;
import org.l5g7.mealcraft.mealcraftexternalrecipesstarter.RecipeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public UserResponseDto getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public void createUser(@RequestBody @Valid UserRequestDto user) {
        userService.createUser(user);
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable Long id, @RequestBody @Valid UserRequestDto user) {
        userService.updateUser(id, user);
    }

    @PatchMapping("/{id}")
    public void patchUser(@PathVariable Long id, @RequestBody @Valid UserRequestDto user) {
        userService.patchUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }

}
