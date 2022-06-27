package com.spring.twitter.api.models.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseModel {
    private long createdAt;
    private long updatedAt;
    private String updatedBy;
}
