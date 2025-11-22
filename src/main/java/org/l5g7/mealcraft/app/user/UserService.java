package org.l5g7.mealcraft.app.user;


import java.util.List;

public interface UserService {
    List<UserResponseDto> getAllUsers();
    UserResponseDto getUserById(Long id);
    UserResponseDto getUserByUsername(String username);
    void createUser(UserRequestDto user);
    void updateUser(Long id, UserRequestDto user);
    void patchUser(Long id, UserRequestDto patch);
    void deleteUserById(Long id);
}
