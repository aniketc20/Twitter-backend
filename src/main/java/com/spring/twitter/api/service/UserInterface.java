package com.spring.twitter.api.service;

import com.spring.twitter.api.dto.FollowFollowerDTO;
import com.spring.twitter.api.models.user.UserModel;
import org.springframework.http.ResponseEntity;

/**
 * @author Aniket
 * @version 1.0
 * @date 30/06/22
 */
public interface UserInterface {
    ResponseEntity<Object> createOrloginUser(UserModel userModel);
    UserModel updateProfile(UserModel userModel);
    ResponseEntity<Object> followUsers(String email);
    ResponseEntity<Object> followUser(FollowFollowerDTO followerDTO);
    ResponseEntity<Object> unfollowUser(FollowFollowerDTO followerDTO);
    ResponseEntity<Object> userFollowing(String user);
    ResponseEntity<Object> userFollowers(String user);
    ResponseEntity<Object> userFeed(String user);
    ResponseEntity<Object> logout(String email);
}
