package com.spring.twitter.api.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Aniket
 * @version 1.0
 * @date 30/06/22
 */
@Getter
@Setter
public class FollowFollowerDTO {
    private String follower;
    private String following;
}
