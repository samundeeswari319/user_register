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
}
