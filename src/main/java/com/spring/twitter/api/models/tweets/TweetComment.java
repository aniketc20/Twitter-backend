package com.spring.twitter.api.models.tweets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spring.twitter.api.models.common.BaseModel;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Aniket
 * @version 1.0
 * @date 30/06/22
 */
@Getter
@Setter
@Document(collection = "TweetComment")
public class TweetComment extends BaseModel {
    @JsonIgnore
    @Id
    private ObjectId objectId;
    private Long id;
    private Long tweetId;
    private String commentedBy;
    private String commenterPic;
    private String commenterEmail;
    private String comment;
    private String mediaFile;
}
