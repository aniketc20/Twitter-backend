package com.spring.twitter.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String name;
    private String pic;
    private String email;
    private String message;
    private Integer status;
}
