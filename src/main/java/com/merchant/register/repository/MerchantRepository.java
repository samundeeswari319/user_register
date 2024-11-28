package com.merchant.register.repository;

import com.merchant.register.model.Merchant;
import com.merchant.register.model.UserRegisterData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepository extends MongoRepository<Merchant,String>{
    /*@Query("{ 'mid': ?0 }")
    Merchant findByMid(String mid);*/

    @Query("{ 'mid': ?0 }")
    Merchant findByMid(String mid);

    @Query("{ 'mid': ?0 }")
    void deleteByMid(String mid);
    @Query("{ 'last_verification_id': ?0 }")
    Merchant findByLastVerificationId(String lastVerificationId);

    @Query("{ 'mobile_number': ?0 }")
    Merchant findByMobileNumber(String mobileNumber);
}
