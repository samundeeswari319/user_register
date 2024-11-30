package com.merchant.register.utils;

import com.merchant.register.common.ErrorResponses;
import com.merchant.register.common.TransactionAPIResponse;
import com.merchant.register.enumclass.ErrorCode;
import com.merchant.register.enumclass.StatusCode;

public class Utils {
    public static String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Extract token without "Bearer " prefix
        }
        return null;
    }

    public static void setUserNotFoundResponse(TransactionAPIResponse response) {
        response.setData(null);
        response.setStatus(false);
        ErrorResponses errorResponse = new ErrorResponses(ErrorCode.USER_NOT_FOUND);
        errorResponse.additionalInfo.excepText =ErrorCode.USER_NOT_FOUND.message;
        response.setError(errorResponse);
        response.setMsg("User Not Found");
        response.setCode(StatusCode.USER_NOT_FOUND.code);
    }
}
