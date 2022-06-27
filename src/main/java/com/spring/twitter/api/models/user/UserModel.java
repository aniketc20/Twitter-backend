package com.spring.twitter.api.models.user;

import com.spring.twitter.api.models.common.BaseModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "Users")
public class UserModel extends BaseModel {
    private String email;
    private String password;
    private String name;
    private String picUrl;
}
