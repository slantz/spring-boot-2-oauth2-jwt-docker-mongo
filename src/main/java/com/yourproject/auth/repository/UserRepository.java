package com.yourproject.auth.repository;

import com.yourproject.auth.model.mongo.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustom {
    Optional<User> findByUsername(String username);
}
