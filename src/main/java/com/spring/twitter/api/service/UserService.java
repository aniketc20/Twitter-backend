package com.spring.twitter.api.service;

import com.spring.twitter.api.dto.FollowFollowerDTO;
import com.spring.twitter.api.dto.UserDTO;
import com.spring.twitter.api.models.user.UserModel;
import com.spring.twitter.api.security.JwtTokenProvider;
import com.spring.twitter.api.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


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
        userInfoResponseDto.setPic(userInfo.getPicUrl());
        userInfoResponseDto.setName(userInfo.getName());
        userInfoResponseDto.setMessage("User Created!");
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

    public ResponseEntity<Object> followUsers(String email) {
        Query query1 = new Query(Criteria.where("email").ne(email));
        List<UserModel> userModel = mongoOperations.find(query1, UserModel.class);
        List<UserDTO> userInfoResponseDto = new ArrayList<>(Collections.emptyList());
        for (UserModel model : userModel) {
            UserDTO userDTO1 = new UserDTO();
            userDTO1.setEmail(model.getEmail());
            userDTO1.setPic(model.getPicUrl());
            userDTO1.setName(model.getName());
            userDTO1.setStatus(HttpStatus.OK.value());
            userInfoResponseDto.add(userDTO1);
        }
        return ResponseEntity.ok().body(userInfoResponseDto);
    }

    @Override
    public ResponseEntity<Object> followUser(FollowFollowerDTO followerDTO) {
        // appends the followed user in the users Following Set
        Query query1 = new Query(Criteria.where(Constants.USER_EMAIL).is(followerDTO.getFollower()));
        Update update1 = new Update();
        update1.addToSet("following", followerDTO.getFollowing());
        mongoOperations.updateFirst(query1, update1, UserModel.class);

        // appends the current user in the followed user's Followers Set
        Query query2 = new Query(Criteria.where(Constants.USER_EMAIL).is(followerDTO.getFollowing()));
        Update update2 = new Update();
        update2.addToSet("followers", followerDTO.getFollower());
        mongoOperations.updateFirst(query2, update2, UserModel.class);

        // Response back to the user
        UserDTO userInfoResponseDto = new UserDTO();
        UserModel logged_in_user = mongoOperations.findOne(query1, UserModel.class);
        userInfoResponseDto.setMessage(logged_in_user.getName()+" is now following "+followerDTO.getFollowing());
        userInfoResponseDto.setName(logged_in_user.getName());
        userInfoResponseDto.setPic(logged_in_user.getPicUrl());
        userInfoResponseDto.setEmail(logged_in_user.getEmail());
        userInfoResponseDto.setStatus(HttpStatus.OK.value());
        return ResponseEntity.status(200).body(userInfoResponseDto);
    }
}
