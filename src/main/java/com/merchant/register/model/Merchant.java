package com.merchant.register.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.merchant.register.common.JsonRequirementsDeserializer;
import com.merchant.register.common.JsonRequirementsSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document("merchant")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Merchant {

    @Transient
    public static final String SEQUENCE_NAME = "merchant_sequence";

    @Id
    public long id;
    public String name;
    public String mid;
    @JsonSerialize(using = JsonRequirementsSerializer.class)
    @JsonDeserialize(using = JsonRequirementsDeserializer.class)
    public String json_requirements;
    private String mobile_number;
    private String send_otp;
    private String last_verification_id;
    private String token;
    private String app_id;
    private String app_name;

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

    public String getJson_requirements() {
        return json_requirements;
    }

    public void setJson_requirements(String json_requirements) {
        this.json_requirements = json_requirements;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }
}