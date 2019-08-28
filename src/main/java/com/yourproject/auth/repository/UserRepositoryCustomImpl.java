package com.yourproject.auth.repository;

import com.yourproject.auth.model.mongo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public UserRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<User> findAndUpdateByUsernames(Collection<User> users) {
        return users
                .stream()
                .map(user -> {
                    Query byUsername = new Query(Criteria.where("username").is(user.getUsername()));
                    Update update = new Update();

                    if (user.getAuthorities() != null && user.getAuthorities().size() > 0) {
                        update.set("authorities", user.getAuthorities());
                    }

                    return this.mongoTemplate.findAndModify(byUsername,
                                                            update,
                                                            new FindAndModifyOptions().returnNew(true),
                                                            User.class);
                })
                .collect(Collectors.toList());
    }
}
