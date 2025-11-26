package org.l5g7.mealcraft.app.user;

import org.l5g7.mealcraft.exception.EntityAlreadyExistsException;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }


    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserResponseDto(
                        u.getId(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getRole(),
                        u.getAvatarUrl()
                ))
                .toList();
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User u = user.get();
            return new UserResponseDto(
                    u.getId(),
                    u.getUsername(),
                    u.getEmail(),
                    u.getRole(),
                    u.getAvatarUrl()
            );
        } else {
            throw new EntityDoesNotExistException("User", String.valueOf(id));
        }
    }

    @Override
    public UserResponseDto getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            User u = user.get();
            return new UserResponseDto(
                    u.getId(),
                    u.getUsername(),
                    u.getEmail(),
                    u.getRole(),
                    u.getAvatarUrl()
            );
        } else {
            throw new EntityDoesNotExistException("User", String.valueOf(username));
        }
    }


    @Override
    public void createUser(UserRequestDto userDto) {
        User newUser = User.builder()
                .username(userDto.username())
                .email(userDto.email())
                .role(userDto.role())
                .password(passwordHasher.hashPassword(userDto.password()))
                .avatarUrl(userDto.avatarUrl())
                .createdAt(new Date())
                .build();
        userRepository.save(newUser);
    }

    @Override
    public void updateUser(Long id, UserRequestDto user) {
        Optional<User> existing = userRepository.findById(id);
        if (existing.isEmpty()) {
            throw new EntityDoesNotExistException("User", String.valueOf(id));
        } else {
            User userToUpdate = existing.get();
            userToUpdate.setUsername(user.username());
            userToUpdate.setEmail(user.email());
            userToUpdate.setRole(user.role());
            if (user.password() != null && !user.password().isEmpty()) {
                String hashedPassword = passwordHasher.hashPassword(user.password());
                userToUpdate.setPassword(hashedPassword);
            }
            userToUpdate.setAvatarUrl(user.avatarUrl());
            userRepository.save(userToUpdate);
        }
    }

    @Override
    public void patchUser(Long id, UserRequestDto patch) {
        Optional<User> existing = userRepository.findById(id);
        if (existing.isEmpty()) {
            throw new EntityDoesNotExistException("User", String.valueOf(id));
        } else {
            User userToPatch = existing.get();
            if (patch.username() != null) {
                userToPatch.setUsername(patch.username());
            }
            if (patch.email() != null) {
                userToPatch.setEmail(patch.email());
            }
            if (patch.role() != null) {
                userToPatch.setRole(patch.role());
            }
            if (patch.password() != null && !patch.password().isEmpty()) {
                String hashedPassword = passwordHasher.hashPassword(patch.password());
                userToPatch.setPassword(hashedPassword);
            }
            if (patch.avatarUrl() != null) {
                userToPatch.setAvatarUrl(patch.avatarUrl());
            }
            userRepository.save(userToPatch);
        }


    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
