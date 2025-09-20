package org.l5g7.mealcraft.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

//2) Використати 4 різні HTTP операції в методах контролеру для позначення різних операцій(читання, редагування, видалення, створення).

@RestController
@RequiredArgsConstructor
public class UserController {

   // private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<String> getUser(@PathVariable int id) {
        String user = "user";
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok(user);
    }
}
