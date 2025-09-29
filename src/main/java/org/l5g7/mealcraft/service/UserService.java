package org.l5g7.mealcraft.service;


import org.l5g7.mealcraft.entity.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    void createUser(User user);
    void updateUser(User user);
    void patchUser(Long id, User patch);
    void deleteUserById(Long id);
}
