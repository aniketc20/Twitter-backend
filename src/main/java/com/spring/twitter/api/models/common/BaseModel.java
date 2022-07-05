package com.spring.twitter.api.models.common;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Aniket
 * @version 1.0
 * @date 30/06/22
 */
@Getter
@Setter
public class BaseModel {
    private long createdAt;
    private long updatedAt;
    private String updatedBy;
}
