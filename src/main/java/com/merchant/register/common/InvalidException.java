package com.merchant.register.common;


import com.merchant.register.enumclass.ErrorCode;

public class InvalidException extends RuntimeException {
    ErrorCode errorCode;

    public InvalidException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }
}
