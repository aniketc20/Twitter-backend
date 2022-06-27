package com.spring.twitter.api.service;

import com.spring.twitter.api.models.tweets.TweetModel;
import org.springframework.http.ResponseEntity;

public interface TweetInterface {
    ResponseEntity<Object> tweet(TweetModel tweet);
    ResponseEntity<Object> getUserTweets();
}
