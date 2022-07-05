package com.spring.twitter.api.repository;

import com.spring.twitter.api.models.user.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Aniket
 * @version 1.0
 * @date 30/06/22
 */
@Repository
public interface UserRepository extends MongoRepository<UserModel, String> {
    UserModel findByPassword(String password);
    @Override
    Optional<UserModel> findById(String userId);
    Boolean existsByEmail(String email);
}
