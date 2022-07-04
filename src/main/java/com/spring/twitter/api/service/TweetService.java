package com.spring.twitter.api.service;

import com.spring.twitter.api.dto.TweetDTO;
import com.spring.twitter.api.models.tweets.TweetModel;
import com.spring.twitter.api.models.user.UserModel;
import com.spring.twitter.api.utils.Constants;
import com.spring.twitter.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
public class TweetService implements TweetInterface {
    @Autowired
    MongoOperations mongoOperations;
    @Override
    public ResponseEntity<Object> tweet(TweetModel tweet) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentLoggedinUser = authentication.getName();
        Query query2 = new Query(Criteria.where(Constants.USER_EMAIL).is(currentLoggedinUser));
        UserModel loggedInUser = mongoOperations.findOne(query2, UserModel.class);
        TweetModel tweetModel = new TweetModel();
        tweetModel.setTweet(tweet.getTweet());
        tweetModel.setUser(tweet.getUser());
        tweetModel.setTweetedBy(loggedInUser.getName());
        tweetModel.setUserPic(loggedInUser.getPicUrl());
        tweetModel.setCreatedAt(Utils.getCurrentTime());
        tweetModel.setUpdatedBy(tweet.getUser());
        if(tweet.getMediaFile()!=null) {
            tweetModel.setMediaFile(tweet.getMediaFile());
        }
        mongoOperations.save(tweetModel);
        TweetDTO tweetDTO = new TweetDTO();
        tweetDTO.setTweet(tweet.getTweet());
        tweetDTO.setEmail(tweet.getUser());
        return ResponseEntity.ok().body(tweetDTO);
    }
    @Override
    public ResponseEntity<Object> getUserDetails(String email) {
        Query query1 = new Query(Criteria.where("user").is(email));
        Query query2 = new Query(Criteria.where("email").is(email));
        UserModel userModel = mongoOperations.findOne(query2, UserModel.class);
        List<TweetModel> tweetModel = mongoOperations.find(query1, TweetModel.class);
        List<TweetDTO> tweetDTO = new java.util.ArrayList<>(Collections.emptyList());
        for (TweetModel model : tweetModel) {
            TweetDTO tweetDTO1 = new TweetDTO();
            Query query = new Query(Criteria.where(Constants.USER_EMAIL).is(model.getUser()));
            UserModel user = mongoOperations.findOne(query, UserModel.class);
            tweetDTO1.setTweet(model.getTweet());
            tweetDTO1.setCreatedAt(model.getCreatedAt());
            tweetDTO1.setUserPic(user.getPicUrl());
            tweetDTO1.setTweetedBy(user.getName());
            tweetDTO1.setMediaFile(model.getMediaFile());
            tweetDTO.add(tweetDTO1);
        }
        HashMap<String,List> map=new HashMap<String, List>();
        List<String> userDetails = new java.util.ArrayList<>(Collections.emptyList());
        userDetails.add(userModel.getName());
        userDetails.add(userModel.getPicUrl());
        userDetails.add(userModel.getEmail());
        Collections.reverse(tweetDTO);
        map.put("tweets", tweetDTO);
        map.put("followers", Collections.singletonList(userModel.getFollowers().stream()));
        map.put("following", Collections.singletonList(userModel.getFollowing().stream()));
        map.put("user_details", userDetails);
        return ResponseEntity.ok().body(map);
    }
}
