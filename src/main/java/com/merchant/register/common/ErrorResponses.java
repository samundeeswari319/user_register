package com.merchant.register.common;


import com.merchant.register.enumclass.ErrorCode;

public class ErrorResponses {
    public AdditionalInfo additionalInfo;

    public ErrorResponses(ErrorCode errorCode) {
        this.additionalInfo = new AdditionalInfo(errorCode.code, errorCode.message);
    }

    public static class AdditionalInfo {
       public String excepCode;
       public String excepText;

        public AdditionalInfo(String excepCode, String excepText) {
            this.excepCode = excepCode;
            this.excepText = excepText;
        }
    }
}
