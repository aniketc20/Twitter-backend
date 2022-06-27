package com.spring.twitter.api.controllers;

import com.spring.twitter.api.models.user.UserModel;
import com.spring.twitter.api.service.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class UserController {
    @Autowired
    UserInterface userInterface;
    @PostMapping("v1/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createOrLoginUser(@RequestBody UserModel userinfo) {
        return userInterface.createOrloginUser(userinfo);
    }
    @PostMapping("v1/updateProfile")
    @ResponseStatus(HttpStatus.CREATED)
    public UserModel updateProfile(@RequestBody UserModel userinfo) {
        return userInterface.updateProfile(userinfo);
    }
}
