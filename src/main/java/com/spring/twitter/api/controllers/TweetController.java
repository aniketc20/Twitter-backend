package com.spring.twitter.api.controllers;

import com.spring.twitter.api.models.tweets.TweetComment;
import com.spring.twitter.api.models.tweets.TweetModel;
import com.spring.twitter.api.service.TweetInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Aniket
 * @version 1.0
 * @date 30/06/22
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class TweetController {
    @Autowired
    TweetInterface tweetInterface;
    @PostMapping("v1/tweet")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> Tweet(@RequestBody TweetModel tweet) {
        return tweetInterface.tweet(tweet);
    }

    @GetMapping("v1/getUserTweets/{email}")
    public ResponseEntity<Object> UserTweets(@PathVariable String email) {
        return tweetInterface.getUserDetails(email);
    }
    @GetMapping("v1/getTweetInfo/{tweetId}")
    public ResponseEntity<Object> getTweetInfo(@PathVariable Long tweetId) {
        return tweetInterface.getTweetInfo(tweetId);
    }
    @PostMapping("v1/{tweetId}/postComment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> TweetComment(@RequestBody TweetComment comment) {
        return tweetInterface.tweetComment(comment);
    }
    @PostMapping("v1/{tweetId}/{email}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> LikeTweet(@PathVariable Long tweetId, @PathVariable String email) {
        System.out.println(email);
        return tweetInterface.likeTweet(tweetId, email);
    }
}
