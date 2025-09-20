//package org.l5g7.mealcraft.service;
//
//import org.l5g7.mealcraft.dao.UserRepository;
//import org.l5g7.mealcraft.entity.User;
//import org.l5g7.mealcraft.exception.UserDoesNotExistException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class UserServiceImpl implements UserService {
//
//    private final UserRepository userRepository;
//
//    @Autowired
//    public UserServiceImpl(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public User find(int id) {
//        Optional<User> result = userRepository.findById(id);
//        User user = null;
//
//        if (result.isPresent()) {
//            user = result.get();
//        } else {
//            throw new UserDoesNotExistException("User with id " + id + " does not exist");
//        }
//        return user;
//    }
//
//    @Override
//    public void save(User user) {
//        userRepository.save(user);
//    }
//
//
//    @Override
//    public void delete(int id) {
//        Optional<User> user = userRepository.findById(id);
//        if (user.isPresent()) {
//            userRepository.delete(user.get());
//        } else {
//            throw new UserDoesNotExistException("User with id " + id + " does not exist");
//        }
//    }
//
//
//}
