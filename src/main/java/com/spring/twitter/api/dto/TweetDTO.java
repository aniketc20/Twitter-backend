package com.spring.twitter.api.dto;

import com.spring.twitter.api.models.tweets.TweetComment;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

/**
 * @author Aniket
 * @version 1.0
 * @date 30/06/22
 */
@Getter
@Setter
public class TweetDTO {
    private Long tweetId;
    private String tweetedBy;
    private String email;
    private String tweet;
    private String userPic;
    private String mediaFile;
    private Long numOfComments;
    private long createdAt;
    private List<TweetComment> comments;
    private Set<String> likedBy;
    private Long numOfLikes;
}
