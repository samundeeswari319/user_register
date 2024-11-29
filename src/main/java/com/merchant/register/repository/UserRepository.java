package com.merchant.register.repository;

import com.merchant.register.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    @Query("{ 'mid': ?0 }")
    List<User> findByMid(String mid);

    @Query("{ 'mid': ?0, 'app_id': ?1 }")
    List<User> findByMidAndAppId(String mid, String app_id);

}
