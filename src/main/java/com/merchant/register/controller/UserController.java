package com.merchant.register.controller;

import com.merchant.register.common.APIResponse;
import com.merchant.register.enumclass.ErrorCode;
import com.merchant.register.model.Merchant;
import com.merchant.register.model.VerifyOtpRequest;
import com.merchant.register.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private MerchantRepository merchantRepository;

    @PostMapping("/verify-otp")
    public APIResponse verifyOtp(@RequestBody VerifyOtpRequest verifyOtpRequest) {
        APIResponse response = new APIResponse();

        try {
            Merchant merchant = merchantRepository.findByMobileNumber(verifyOtpRequest.getMobileNumber());
            if (merchant == null) {
                response.setStatus(false);
                response.setCode(400);
                response.setData(null);
                response.setError(ErrorCode.INVALID_VERIFICATION_ID.code);
                response.setMsg("Invalid lastVerificationId.");
                return response;
            }

            if ("1111".equals(verifyOtpRequest.getOtp())) {
                merchant.setSend_otp("1");
                merchant.setLast_verification_id(verifyOtpRequest.getLastVerificationId());
                merchantRepository.save(merchant);
                response.setStatus(true);
                response.setCode(200);
                response.setData(merchant);
                response.setError(null);
                response.setMsg("OTP Verified Successfully");
            } else {
                response.setStatus(false);
                response.setCode(400);
                response.setData(null);
                response.setError(ErrorCode.INVALID_OTP.code);
                response.setMsg("Invalid OTP");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(false);
            response.setCode(500);
            response.setData(null);
            response.setError("Internal server error");
            response.setMsg("An error occurred during OTP verification. Please try again later.");
        }

        return response;
    }
}