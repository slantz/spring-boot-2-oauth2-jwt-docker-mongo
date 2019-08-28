package com.yourproject.auth.repository;

import com.yourproject.auth.model.mongo.User;

import java.util.Collection;
import java.util.List;

public interface UserRepositoryCustom {
    List<User> findAndUpdateByUsernames(Collection<User> users);
}
