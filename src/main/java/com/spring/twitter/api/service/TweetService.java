package com.spring.twitter.api.service;

import com.spring.twitter.api.dto.TweetDTO;
import com.spring.twitter.api.models.tweets.TweetComment;
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

/**
 * @author Aniket
 * @version 1.0
 * @date 30/06/22
 */
@Service
public class TweetService implements TweetInterface {
    @Autowired
    MongoOperations mongoOperations;
    @Override
    public ResponseEntity<Object> tweet(TweetModel tweet) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentLoggedinUser = authentication.getName();
        Query query = new Query(Criteria.where(Constants.USER_EMAIL).is(currentLoggedinUser));
        UserModel loggedInUser = mongoOperations.findOne(query, UserModel.class);
        TweetModel tweetModel = new TweetModel();
        tweetModel.setTweetId(Utils.generateId());
        tweetModel.setTweet(tweet.getTweet());
        tweetModel.setTweeterEmail(tweet.getTweeterEmail());
        tweetModel.setTweetedBy(loggedInUser.getName());
        tweetModel.setTweeterPic(loggedInUser.getPicUrl());
        tweetModel.setCreatedAt(Utils.getCurrentTime());
        tweetModel.setUpdatedBy(tweet.getTweeterEmail());
        if(tweet.getMediaFile()!=null) {
            tweetModel.setMediaFile(tweet.getMediaFile());
        }
        mongoOperations.save(tweetModel);
        TweetDTO tweetDTO = new TweetDTO();
        tweetDTO.setTweet(tweet.getTweet());
        tweetDTO.setEmail(tweet.getTweeterEmail());
        return ResponseEntity.ok().body(tweetDTO);
    }
    public ResponseEntity<Object> tweetComment(TweetComment comment) {
        TweetComment insertComment = new TweetComment();
        insertComment.setTweetId(comment.getTweetId());
        insertComment.setComment(comment.getComment());
        insertComment.setCommentedBy(comment.getCommentedBy());
        insertComment.setCommenterPic(comment.getCommenterPic());
        insertComment.setCommenterEmail(comment.getCommenterEmail());
        mongoOperations.save(insertComment);
        return ResponseEntity.ok().body(insertComment);
    }
    @Override
    public ResponseEntity<Object> getUserDetails(String email) {
        Query query1 = new Query(Criteria.where(Constants.TWEETER_EMAIL).is(email));
        Query query2 = new Query(Criteria.where(Constants.USER_EMAIL).is(email));
        UserModel userModel = mongoOperations.findOne(query2, UserModel.class);
        List<TweetModel> tweetModel = mongoOperations.find(query1, TweetModel.class);
        List<TweetDTO> tweetDTO = new java.util.ArrayList<>(Collections.emptyList());
        for (TweetModel model : tweetModel) {
            TweetDTO tweetDTO1 = new TweetDTO();
            Query query = new Query(Criteria.where(Constants.USER_EMAIL).is(model.getTweeterEmail()));
            UserModel user = mongoOperations.findOne(query, UserModel.class);
            Query query3 = new Query(Criteria.where(Constants.TWEET_ID).is(model.getTweetId()));
            List<TweetComment> comments = mongoOperations.find(query3, TweetComment.class);
            tweetDTO1.setTweet(model.getTweet());
            tweetDTO1.setCreatedAt(model.getCreatedAt());
            tweetDTO1.setUserPic(user.getPicUrl());
            tweetDTO1.setTweetedBy(user.getName());
            tweetDTO1.setMediaFile(model.getMediaFile());
            tweetDTO1.setComments(comments);
            tweetDTO1.setTweetId(model.getTweetId());
            tweetDTO1.setNumOfComments(comments.stream().count());
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
    public ResponseEntity<Object> getTweetInfo(Long tweetId) {
        Query query = new Query(Criteria.where(Constants.TWEET_ID).is(tweetId));
        TweetModel tweet = mongoOperations.findOne(query, TweetModel.class);
        List<TweetComment> comments = mongoOperations.find(query, TweetComment.class);
        TweetDTO tweetDTO = new TweetDTO();
        tweetDTO.setTweetId(tweet.getTweetId());
        tweetDTO.setTweet(tweet.getTweet());
        tweetDTO.setTweetedBy(tweet.getTweetedBy());
        tweetDTO.setEmail(tweet.getTweeterEmail());
        tweetDTO.setUserPic(tweet.getTweeterPic());
        tweetDTO.setMediaFile(tweet.getMediaFile());
        tweetDTO.setNumOfComments(comments.stream().count());
        tweetDTO.setComments(comments);
        System.out.println(tweet);
        return ResponseEntity.ok().body(tweetDTO);
    }
}
