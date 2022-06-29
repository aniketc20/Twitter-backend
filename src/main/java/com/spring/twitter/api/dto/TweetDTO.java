package com.spring.twitter.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TweetDTO {
    private String tweetedBy;
    private String email;
    private String tweet;
    private String userPic;
    private String mediaFile;
    private long createdAt;
}
