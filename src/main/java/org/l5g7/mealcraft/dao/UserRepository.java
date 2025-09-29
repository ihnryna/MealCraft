package org.l5g7.mealcraft.dao;

import org.l5g7.mealcraft.entity.User;

import java.util.List;
import java.util.Optional;


public interface UserRepository {
    List<User> findAll();
    Optional<User> findById(Long id);
    void create(User user);
    boolean update(User user);
    boolean deleteById(Long id);
}
