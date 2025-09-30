package org.l5g7.mealcraft.service;

import org.l5g7.mealcraft.dao.UserRepository;
import org.l5g7.mealcraft.entity.User;
import org.l5g7.mealcraft.exception.EntityAlreadyExistsException;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    private PasswordHasher passwordHasher;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new EntityDoesNotExistException("User", id);
        }
    }

    @Override
    public void createUser(User user) {
        Optional<User> theUser = userRepository.findById(user.getId());
        if (theUser.isPresent()) {
            throw new EntityAlreadyExistsException("User", user.getId());
        } else {
            user.setPassword(passwordHasher.hashPassword(user.getPassword()));
            userRepository.create(user);
        }
    }


    @Override
    public void updateUser(User user) {
        if (user.getPassword() != null) {
            user.setPassword(passwordHasher.hashPassword(user.getPassword()));
        }
        boolean updated = userRepository.update(user);
        if (!updated) {
            throw new EntityDoesNotExistException("User", user.getId());
        }
    }

    @Override
    public void patchUser(Long id, User patch) {
        User existing = getUserById(id);
        if (patch.getUsername() != null) existing.setUsername(patch.getUsername());
        if (patch.getEmail() != null)    existing.setEmail(patch.getEmail());
        if (patch.getRole() != null)     existing.setRole(patch.getRole());
        if (patch.getPassword() != null) existing.setPassword(passwordHasher.hashPassword(patch.getPassword()));
        if (patch.getAvatarUrl() != null) existing.setAvatarUrl(patch.getAvatarUrl());
        updateUser(existing);
    }


    @Override
    public void deleteUserById(Long id) {
        boolean deleted = userRepository.deleteById(id);
        if (!deleted) {
            throw new EntityDoesNotExistException("User", id);
        }
    }
}
