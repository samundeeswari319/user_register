package com.merchant.register.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;

@Document("user")
@Getter
@Setter
public class User {
    @Transient
    public static final String SEQUENCE_NAME = "merchant_sequence";

    @Id
    public long id;
    public HashMap<String,Object> user_details;
    public String mid;
    public String app_id;
    public String app_name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public HashMap<String, Object> getUser_details() {
        return user_details;
    }

    public void setUser_details(HashMap<String, Object> user_details) {
        this.user_details = user_details;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
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
