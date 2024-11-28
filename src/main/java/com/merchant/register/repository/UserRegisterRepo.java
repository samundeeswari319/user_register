package com.merchant.register.repository;

import com.merchant.register.model.UserRegisterData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRegisterRepo extends MongoRepository<UserRegisterData, String> {
    @Query("{ 'mid': ?0 }")
    UserRegisterData findByMid(String mid);
}
