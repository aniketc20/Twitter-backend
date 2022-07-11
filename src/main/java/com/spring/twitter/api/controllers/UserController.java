package com.spring.twitter.api.controllers;

import com.spring.twitter.api.dto.FollowFollowerDTO;
import com.spring.twitter.api.dto.TweetDTO;
import com.spring.twitter.api.models.user.UserModel;
import com.spring.twitter.api.service.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Aniket
 * @version 1.0
 * @date 30/06/22
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class UserController {
    @Autowired
    UserInterface userInterface;
    private final ExecutorService bakers = Executors.newFixedThreadPool(5);
    @PostMapping("v1/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createOrLoginUser(@RequestBody UserModel userinfo) {
        return userInterface.createOrloginUser(userinfo);
    }
    @PostMapping("v1/{email}/logout")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> logout(@PathVariable String email) {
        return userInterface.logout(email);
    }
    @PostMapping("v1/updateProfile")
    @ResponseStatus(HttpStatus.CREATED)
    public UserModel updateProfile(@RequestBody UserModel userinfo) {
        System.out.println(userinfo.getName());
        return userInterface.updateProfile(userinfo);
    }
    @GetMapping("v1/followusers/{email}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> followUsers(@PathVariable String email) {
        return userInterface.followUsers(email);
    }
    @PostMapping("v1/follow")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> followUser(@RequestBody FollowFollowerDTO followerDTO) {
        return userInterface.followUser(followerDTO);
    }
    @PostMapping("v1/unfollow")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> unfollowUser(@RequestBody FollowFollowerDTO followerDTO) {
        return userInterface.unfollowUser(followerDTO);
    }
    @GetMapping("v1/{user}/following")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> userFollowing(@PathVariable String user) {
        return userInterface.userFollowing(user);
    }
    @GetMapping("v1/{user}/followers")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> userFollowers(@PathVariable String user) {
        return userInterface.userFollowers(user);
    }
    @GetMapping("v1/{user}/feed")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> userFeed(@PathVariable String user) {
        return userInterface.userFeed(user);
    }
    @GetMapping("v1/poll")
    public DeferredResult<Object> userFeedLongPolling(@RequestParam String user) {
        DeferredResult<Object> output = new DeferredResult<>(120000L);
        output.onTimeout(() -> output.setErrorResult("no updates"));
        bakers.execute(() -> {
            try {
                //Thread.sleep(10);
                List<TweetDTO> tweetDTO = (List<TweetDTO>) userInterface.userFeed(user).getBody();
                int initialSize  = tweetDTO.size();
                int newSize;
                while (true) {
                    tweetDTO = (List<TweetDTO>) userInterface.userFeed(user).getBody();
                    newSize = tweetDTO.size();
                    if(newSize!=initialSize) {
                        break;
                    }
                }
                output.setResult(userInterface.userFeed(user));
            } catch (Exception e) {
                // ...
            }
        });
        return output;
    }
}
