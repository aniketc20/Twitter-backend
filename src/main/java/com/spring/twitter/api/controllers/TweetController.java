package com.spring.twitter.api.controllers;

import com.spring.twitter.api.models.tweets.TweetModel;
import com.spring.twitter.api.service.TweetInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("v1/getUserTweets")
    public ResponseEntity<Object> UserTweets() {
        return tweetInterface.getUserTweets();
    }
}
