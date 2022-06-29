package com.spring.twitter.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowFollowerDTO {
    private String follower;
    private String following;
}
