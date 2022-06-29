package com.spring.twitter.api.models.tweets;

import com.spring.twitter.api.models.common.BaseModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "Tweet")
public class TweetModel extends BaseModel {
    private String tweet;
    private String mediaFile;
    private String user;
}
