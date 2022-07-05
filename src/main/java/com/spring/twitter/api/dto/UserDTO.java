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
public class UserDTO {
    private String name;
    private String pic;
    private String email;
    private String message;
    private Integer status;
    private Long followers;
    private Long following;
}
