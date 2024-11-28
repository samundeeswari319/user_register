package com.merchant.register.repository;

import com.merchant.register.model.User;
import com.merchant.register.model.UserRegisterData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.HashMap;

public interface UserRepository extends MongoRepository<User, String> {
}
