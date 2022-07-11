package com.spring.twitter.api.service;

import com.spring.twitter.api.models.tweets.TweetComment;
import com.spring.twitter.api.models.tweets.TweetModel;
import org.springframework.http.ResponseEntity;

/**
 * @author Aniket
 * @version 1.0
 * @date 30/06/22
 */
public interface TweetInterface {
    ResponseEntity<Object> tweet(TweetModel tweet);
    ResponseEntity<Object> getUserDetails(String email);
    ResponseEntity<Object> tweetComment(TweetComment comment);
    ResponseEntity<Object> getTweetInfo(Long tweetId);
    ResponseEntity<Object> likeTweet(Long tweetId, String email);
}
