package com.merchant.register.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;

@Document("merchant")
public class Merchant {

    @Transient
    public static final String SEQUENCE_NAME = "merchant_sequence";

    @Id
    public long id;
    public String name;
    public String mid;
    public HashMap<String,Object> requirements;
    public String json_requirements;
    private String mobile_number;
    private String send_otp;
    private String last_verification_id;
    private String token;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public HashMap<String, Object> getRequirements() {
        return requirements;
    }

    public void setRequirements(HashMap<String, Object> requirements) {
        this.requirements = requirements;
    }

    public String getSend_otp() {
        return send_otp;
    }

    public void setSend_otp(String send_otp) {
        this.send_otp = send_otp;
    }

    public String getLast_verification_id() {
        return last_verification_id;
    }

    public void setLast_verification_id(String last_verification_id) {
        this.last_verification_id = last_verification_id;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}