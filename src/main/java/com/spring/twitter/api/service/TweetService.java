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
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class TweetService implements TweetInterface {
    @Autowired
    MongoOperations mongoOperations;
    @Override
    public ResponseEntity<Object> tweet(TweetModel tweet) {
        TweetModel tweetModel = new TweetModel();
        tweetModel.setTweet(tweet.getTweet());
        tweetModel.setUser(tweet.getUser());
        tweetModel.setCreatedAt(Utils.getCurrentTime());
        tweetModel.setUpdatedBy(tweet.getUser());
        mongoOperations.save(tweetModel);
        TweetDTO tweetDTO = new TweetDTO();
        tweetDTO.setTweet(tweet.getTweet());
        tweetDTO.setEmail(tweet.getUser());
        return ResponseEntity.ok().body(tweetDTO);
    }
    @Override
    public ResponseEntity<Object> getUserTweets() {
        List<TweetModel> tweetModel = mongoOperations.findAll(TweetModel.class);
        List<TweetDTO> tweetDTO = new java.util.ArrayList<>(Collections.emptyList());
        for (TweetModel model : tweetModel) {
            TweetDTO tweetDTO1 = new TweetDTO();
            Query query = new Query(Criteria.where(Constants.USER_EMAIL).is(model.getUser()));
            UserModel user = mongoOperations.findOne(query, UserModel.class);
            tweetDTO1.setTweet(model.getTweet());
            tweetDTO1.setCreatedAt(model.getCreatedAt());
            tweetDTO1.setUserPic(user.getPicUrl());
            tweetDTO1.setUserName(user.getName());
            tweetDTO.add(tweetDTO1);
        }

        return ResponseEntity.ok().body(tweetDTO);
    }
}
