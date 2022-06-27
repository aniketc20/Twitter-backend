package com.spring.twitter.api.service;

import com.spring.twitter.api.dto.UserDTO;
import com.spring.twitter.api.models.user.UserModel;
import com.spring.twitter.api.security.JwtTokenProvider;
import com.spring.twitter.api.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService implements UserInterface{
    @Autowired
    MongoOperations mongoOperations;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Override
    public ResponseEntity<Object> createOrloginUser(UserModel userInfo) {
        if (isRegistered(userInfo)) {
            UserDTO userInfoResponseDto = new UserDTO();
            Query query = new Query(Criteria.where(Constants.USER_EMAIL).is(userInfo.getEmail()));
            UserModel user = mongoOperations.findOne(query, UserModel.class);
            userInfoResponseDto.setMessage("User exists!");
            userInfoResponseDto.setEmail(user.getEmail());
            userInfoResponseDto.setPic(user.getPicUrl());
            String tokenHeader = jwtTokenProvider.createToken(userInfo.getEmail(), userInfo.getName(), false, false);
            return ResponseEntity.ok().header(Constants.TOKEN_HEADER, tokenHeader).header("Access-Control-Expose-Headers", Constants.TOKEN_HEADER).body(userInfoResponseDto);
        }
        UserModel userModel = new UserModel();
        userModel.setEmail(userInfo.getEmail());
        userModel.setName(userInfo.getName());
        userModel.setPicUrl(userInfo.getPicUrl());
        userModel.setPassword(passwordEncoder.encode(userInfo.getEmail()));
        UserDTO userInfoResponseDto = new UserDTO();
        userInfoResponseDto.setEmail(userInfo.getEmail());
        mongoOperations.save(userModel);
        String tokenHeader = jwtTokenProvider.createToken(userInfo.getEmail(), userInfo.getName(), false, false);
        return ResponseEntity.ok().header(Constants.TOKEN_HEADER, tokenHeader).body(userInfoResponseDto);
    }
    private boolean isRegistered(UserModel userModel) {
        Query query = new Query(Criteria.where(Constants.USER_EMAIL).is(userModel.getEmail()));
        UserModel user = mongoOperations.findOne(query, UserModel.class);
        return user != null;
    }
    public UserModel updateProfile(UserModel userModel) {
        Query query = new Query(Criteria.where(Constants.USER_EMAIL).is(userModel.getEmail()));
        UserModel current_user = mongoOperations.findOne(query, UserModel.class);
        Update update = new Update();
        update.set("picUrl", userModel.getPicUrl());
        mongoOperations.updateFirst(query, update, UserModel.class);
        return current_user;
    }
}
