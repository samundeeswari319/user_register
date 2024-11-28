package com.merchant.register.utils;

public class Utils {
    public static String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Extract token without "Bearer " prefix
        }
        return null;
    }
}
