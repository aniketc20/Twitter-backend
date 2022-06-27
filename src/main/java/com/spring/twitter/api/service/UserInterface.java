package com.spring.twitter.api.service;

import com.spring.twitter.api.models.user.UserModel;
import org.springframework.http.ResponseEntity;

public interface UserInterface {
    ResponseEntity<Object> createOrloginUser(UserModel userModel);
    UserModel updateProfile(UserModel userModel);
}
