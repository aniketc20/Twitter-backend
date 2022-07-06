package com.spring.twitter.api.service;

import com.spring.twitter.api.dto.FollowFollowerDTO;
import com.spring.twitter.api.dto.TweetDTO;
import com.spring.twitter.api.dto.UserDTO;
import com.spring.twitter.api.models.tweets.TweetComment;
import com.spring.twitter.api.models.tweets.TweetModel;
import com.spring.twitter.api.models.user.UserModel;
import com.spring.twitter.api.security.JwtTokenProvider;
import com.spring.twitter.api.security.UserAuthDetails;
import com.spring.twitter.api.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Aniket
 * @version 1.0
 * @date 30/06/22
 */
@Service
public class UserService implements UserInterface{
    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;
    @Autowired
    MongoOperations mongoOperations;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserAuthDetails userAuthDetails;
    @Override
    public ResponseEntity<Object> createOrloginUser(UserModel userInfo) {
        if (isRegistered(userInfo)) {
            UserDTO userInfoResponseDto = new UserDTO();
            Query query = new Query(Criteria.where(Constants.USER_EMAIL).is(userInfo.getEmail()));
            UserModel user = mongoOperations.findOne(query, UserModel.class);
            userInfoResponseDto.setMessage("User exists!");
            userInfoResponseDto.setEmail(user.getEmail());
            userInfoResponseDto.setName(user.getName());
            userInfoResponseDto.setPic(user.getPicUrl());
            String tokenHeader = jwtTokenProvider.createToken(userInfo.getEmail(), user.getPassword(), false, false);
            return ResponseEntity.ok().header(Constants.TOKEN_HEADER, tokenHeader).header("Access-Control-Expose-Headers", Constants.TOKEN_HEADER).body(userInfoResponseDto);
        }
        UserModel userModel = new UserModel();
        userModel.setEmail(userInfo.getEmail());
        userModel.setName(userInfo.getName());
        userModel.setPicUrl(userInfo.getPicUrl());
        userModel.setPassword(passwordEncoder.encode(userInfo.getEmail()));
        Set<String> EmptySet = Collections.emptySet();
        userModel.setFollowers(EmptySet);
        userModel.setFollowing(EmptySet);
        UserDTO userInfoResponseDto = new UserDTO();
        userInfoResponseDto.setEmail(userInfo.getEmail());
        userInfoResponseDto.setPic(userInfo.getPicUrl());
        userInfoResponseDto.setName(userInfo.getName());
        userInfoResponseDto.setMessage("User Created!");
        mongoOperations.save(userModel);
        String tokenHeader = jwtTokenProvider.createToken(userInfo.getEmail(), userModel.getPassword(), false, false);
        return ResponseEntity.ok().header(Constants.TOKEN_HEADER, tokenHeader).header("Access-Control-Expose-Headers", Constants.TOKEN_HEADER).body(userInfoResponseDto);
    }
    public ResponseEntity<Object> logout(String email) {
        Query query = new Query(Criteria.where(Constants.USER_EMAIL).is(email));
        UserModel user = mongoOperations.findOne(query, UserModel.class);
        Update update = new Update();
        update.set("password", passwordEncoder.encode(email));
        mongoOperations.updateFirst(query, update, UserModel.class);
        return ResponseEntity.ok().body(user);
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
        update.set("name", userModel.getName());
        mongoOperations.updateFirst(query, update, UserModel.class);
        return current_user;
    }

    public ResponseEntity<Object> followUsers(String email) {
        Query query1 = new Query(Criteria.where(Constants.USER_EMAIL).ne(email));
        Query query2 = new Query(Criteria.where(Constants.USER_EMAIL).is(email));
        UserModel userModel1 = mongoOperations.findOne(query2, UserModel.class);
        List<UserModel> userModel = mongoOperations.find(query1, UserModel.class);
        List<UserDTO> userInfoResponseDto = new ArrayList<>(Collections.emptyList());
        for (UserModel model : userModel) {
            if (!userModel1.getFollowing().contains(model.getEmail())) {
                UserDTO userDTO1 = new UserDTO();
                userDTO1.setEmail(model.getEmail());
                userDTO1.setPic(model.getPicUrl());
                userDTO1.setName(model.getName());
                userDTO1.setStatus(HttpStatus.OK.value());
                userInfoResponseDto.add(userDTO1);
            }
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

    @Override
    public ResponseEntity<Object> unfollowUser(FollowFollowerDTO followerDTO) {
        // appends the followed user in the users Following Set
        Query query1 = new Query(Criteria.where(Constants.USER_EMAIL).is(followerDTO.getFollower()));
        Update update1 = new Update();
        update1.pull("following",followerDTO.getFollowing());
        mongoOperations.updateFirst(query1, update1, UserModel.class);

        // appends the current user in the followed user's Followers Set
        Query query2 = new Query(Criteria.where(Constants.USER_EMAIL).is(followerDTO.getFollowing()));
        Update update2 = new Update();
        update2.pull("followers",followerDTO.getFollower());
        mongoOperations.updateFirst(query2, update2, UserModel.class);

        // Response back to the user
        UserDTO userInfoResponseDto = new UserDTO();
        UserModel logged_in_user = mongoOperations.findOne(query1, UserModel.class);
        userInfoResponseDto.setMessage(logged_in_user.getName()+" is now unfollowing "+followerDTO.getFollowing());
        userInfoResponseDto.setName(logged_in_user.getName());
        userInfoResponseDto.setPic(logged_in_user.getPicUrl());
        userInfoResponseDto.setEmail(logged_in_user.getEmail());
        userInfoResponseDto.setStatus(HttpStatus.OK.value());
        return ResponseEntity.status(200).body(userInfoResponseDto);
    }

    @Override
    public ResponseEntity<Object> userFollowing(String user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentLoggedinUser = authentication.getName();
        Query query = new Query(Criteria.where(Constants.USER_EMAIL).is(user));
        Query query2 = new Query(Criteria.where(Constants.USER_EMAIL).is(currentLoggedinUser));
        UserModel userModel = mongoOperations.findOne(query, UserModel.class);
        UserModel loggedInUser = mongoOperations.findOne(query2, UserModel.class);
        List<UserDTO> userInfoResponseDto = new ArrayList<>(Collections.emptyList());
        for (String model : userModel.getFollowing()) {
            UserDTO userDTO1 = new UserDTO();
            Query query1 = new Query(Criteria.where(Constants.USER_EMAIL).is(model));
            UserModel user_following = mongoOperations.findOne(query1, UserModel.class);
            userDTO1.setEmail(user_following.getEmail());
            userDTO1.setPic(user_following.getPicUrl());
            userDTO1.setName(user_following.getName());
            userDTO1.setMessage("Follow");
            if(loggedInUser.getFollowing().contains(user_following.getEmail())) {
                userDTO1.setMessage("Unfollow");
            }
            if(Objects.equals(user_following.getEmail(), currentLoggedinUser)) {
                userDTO1.setMessage("Logged in user");
            }
            userDTO1.setStatus(HttpStatus.OK.value());
            userInfoResponseDto.add(userDTO1);
        }
        return ResponseEntity.status(200).body(userInfoResponseDto);
    }
    @Override
    public ResponseEntity<Object> userFollowers(String user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentLoggedUser = authentication.getName();
        Query query = new Query(Criteria.where(Constants.USER_EMAIL).is(user));
        Query query2 = new Query(Criteria.where(Constants.USER_EMAIL).is(currentLoggedUser));
        UserModel userModel = mongoOperations.findOne(query, UserModel.class);
        UserModel loggedInUser = mongoOperations.findOne(query2, UserModel.class);
        List<UserDTO> userInfoResponseDto = new ArrayList<>(Collections.emptyList());
        for (String model : userModel.getFollowers()) {
            UserDTO userDTO1 = new UserDTO();
            Query query1 = new Query(Criteria.where(Constants.USER_EMAIL).is(model));
            UserModel user_followers = mongoOperations.findOne(query1, UserModel.class);
            userDTO1.setEmail(user_followers.getEmail());
            userDTO1.setPic(user_followers.getPicUrl());
            userDTO1.setName(user_followers.getName());
            userDTO1.setMessage("Follow");
            if(loggedInUser.getFollowing().contains(user_followers.getEmail())) {
                userDTO1.setMessage("Unfollow");
            }
            if(Objects.equals(user_followers.getEmail(), currentLoggedUser)) {
                userDTO1.setMessage("Logged in user");
            }
            userDTO1.setStatus(HttpStatus.OK.value());
            userInfoResponseDto.add(userDTO1);
        }
        return ResponseEntity.status(200).body(userInfoResponseDto);

    }
    @Override
    public ResponseEntity<Object> userFeed(String user) {
        List<TweetModel> tweets = mongoOperations.findAll(TweetModel.class);
        List<TweetDTO> tweetDTO = new java.util.ArrayList<>(Collections.emptyList());
        Query query1 = new Query(Criteria.where(Constants.USER_EMAIL).is(user));
        UserModel loggedInUser = mongoOperations.findOne(query1, UserModel.class);
        for (TweetModel tweet : tweets) {
            TweetDTO tweetDTO1 = new TweetDTO();
            Query query2 = new Query(Criteria.where(Constants.USER_EMAIL).is(tweet.getTweeterEmail()));
            UserModel tweetedBy = mongoOperations.findOne(query2, UserModel.class);
            if(loggedInUser.getFollowing().contains(tweetedBy.getEmail()) || Objects.equals(tweetedBy.getEmail(), user)) {
                Query query = new Query(Criteria.where(Constants.TWEET_ID).is(tweet.getTweetId()));
                List<TweetComment> comments = mongoOperations.find(query, TweetComment.class);
                tweetDTO1.setTweet(tweet.getTweet());
                tweetDTO1.setCreatedAt(tweet.getCreatedAt());
                tweetDTO1.setUserPic(tweetedBy.getPicUrl());
                tweetDTO1.setTweetedBy(tweetedBy.getName());
                tweetDTO1.setMediaFile(tweet.getMediaFile());
                tweetDTO1.setComments(comments);
                tweetDTO1.setTweetId(tweet.getTweetId());
                tweetDTO1.setNumOfComments(comments.stream().count());
                tweetDTO.add(tweetDTO1);
            }
        }
        Collections.reverse(tweetDTO);
        return ResponseEntity.status(200).body(tweetDTO);
    }
}
