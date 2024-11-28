package com.merchant.register.controller;

import com.merchant.register.common.APIResponse;
import com.merchant.register.common.ErrorResponses;
import com.merchant.register.common.InvalidException;
import com.merchant.register.config.JwtUtils;
import com.merchant.register.enumclass.ErrorCode;
import com.merchant.register.enumclass.StatusCode;
import com.merchant.register.model.AuthTokenModel;
import com.merchant.register.model.Merchant;
import com.merchant.register.model.User;
import com.merchant.register.model.UserRegisterData;
import com.merchant.register.repository.MerchantRepository;
import com.merchant.register.repository.UserRegisterRepo;
import com.merchant.register.repository.UserRepository;
import com.merchant.register.services.SequenceGeneratorService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.merchant.register.utils.Utils.extractTokenFromHeader;

@RestController
/*@RequestMapping("/v1/api")*/
public class MerchantController {

    @Autowired
    UserRegisterRepo userRegisterRepo;
    @Autowired
    SequenceGeneratorService sequenceGeneratorService;
    @Autowired
    MerchantRepository merchantRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AuthTokenModel authTokenModel;

    @PostMapping("/login/auth/create_merchant")
    public APIResponse createMerchant(@RequestBody Merchant model) {
        String authToken;
        APIResponse response = new APIResponse();

        try {
            /*if (model.getMerchant_id() == null || model.getMerchant_id().isEmpty()) {
                response.setStatus(false);
                response.setCode(400);
                response.setData(null);
                response.setError(ErrorCode.INVALID_MERCHANT.code);
                response.setMsg("Merchant ID cannot be null or empty.");
                return response;
            }*/
           /* if (merchantRepository.findByMid(model.getMerchant_id()) != null) {
                response.setStatus(false);
                response.setCode(400);
                response.setData(null);
                response.setError(ErrorCode.MERCHANT_DUPLICATE.code);
                response.setMsg("Duplicate merchant ID.");
                return response;
            }*/
            long id = sequenceGeneratorService.generateSequence(Merchant.SEQUENCE_NAME);
            model.setSend_otp("0");
            model.setLast_verification_id("0");
            model.setId(id);
            authToken = jwtUtils.createToken(model);
            model.setToken(authToken);
            model.setMid(String.valueOf(id));
            Merchant savedModel = merchantRepository.save(model);
            response.setStatus(true);
            response.setCode(200);
            response.setData(savedModel);
            response.setError(null);
            response.setMsg("Merchant created successfully.");

        } catch (Exception e) {
            response.setStatus(false);
            response.setCode(500);
            response.setData(null);
            response.setError("Internal server error");
            response.setMsg(e.getMessage());
        }

        return response;
    }

    @PostMapping("/add_register_data")
    private APIResponse getUserRegisterData(@RequestBody UserRegisterData userRegisterData) throws IOException {
        APIResponse apiResponse = new APIResponse();
        userRegisterData.id = sequenceGeneratorService.generateSequence(UserRegisterData.SEQUENCE_NAME);
        userRegisterRepo.save(userRegisterData);
        apiResponse.setStatus(true);
        apiResponse.setMsg("Success!!");
        apiResponse.setData(userRegisterData);
        return apiResponse;
    }



    @PostMapping("edit_merchant/{mid}")
    public APIResponse updateMerchant(@RequestBody Merchant updatedModel,@PathVariable("mid") String mid) {
        APIResponse response = new APIResponse();
        try {
            Merchant existingModel = merchantRepository.findByMid(mid);
            if (existingModel != null) {
                System.out.println("Existing merchant ID: " + existingModel.id);
                Merchant model = existingModel;
                model.setName(updatedModel.getName());
                model.setRequirements(updatedModel.getRequirements());
                Merchant savedModel = merchantRepository.save(model);
                response.setStatus(true);
                response.setCode(200);
                response.setData(savedModel);
                response.setError(null);
                response.setMsg("Merchant updated successfully.");
            } else {
                response.setStatus(false);
                response.setCode(404);
                response.setData(null);
                response.setError("MERCHANT_NOT_FOUND");
                response.setMsg("Merchant not found for the given ID.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(false);
            response.setCode(500);
            response.setData(null);
            response.setError("INTERNAL_SERVER_ERROR");
            response.setMsg("An error occurred while updating the merchant: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("delete_merchant/{mid}")
    public ResponseEntity<APIResponse> deleteMerchant(@PathVariable("mid") String mid) {
        APIResponse response = new APIResponse();
        Merchant merchant = merchantRepository.findByMid(mid);
        if (merchant != null) {
            merchantRepository.delete(merchant);
            response.setCode(HttpStatus.OK.value());
            response.setStatus(true);
            response.setMsg("Merchant deleted successfully");
        } else {
            response.setCode(HttpStatus.NOT_FOUND.value());
            response.setStatus(false);
            response.setMsg("Merchant not found");
        }
        return ResponseEntity.ok(response);
    }


//for merchant to select fields in page
    @PostMapping("/user_register_data")
    private APIResponse showUserRequirements(@RequestHeader("Authorization") String token){
        APIResponse apiResponse = new APIResponse();
            if(token !=null){
                List<UserRegisterData> userRegisterData = userRegisterRepo.findAll();
                UserRegisterData registerData = userRegisterData.get(0);
                //registerData
                apiResponse.setStatus(true);
                apiResponse.setMsg("Success!!");
                apiResponse.setCode(StatusCode.SUCCESS.code);
                apiResponse.setData(registerData);
            }
            return apiResponse;
    }

    @PostMapping("/selected_fields")
    private APIResponse getSelectedFields(@RequestBody HashMap<String,Object> fields){
        APIResponse apiResponse = new APIResponse();
        Merchant merchant = merchantRepository.findByMid(authTokenModel.user_id);
        if(merchant != null){
            //String midL = fields.get("mid").toString();
            merchant.requirements = new HashMap<>(fields);
            merchantRepository.save(merchant);
        }
        return apiResponse;
    }

    @PostMapping("/register")
    private APIResponse userRegisterData(@RequestBody HashMap<String,Object> register) throws IOException {
        APIResponse apiResponse = new APIResponse();
            //String midL = register.get("mid").toString();
            Merchant merchant = merchantRepository.findByMid(authTokenModel.user_id);
            if(merchant != null){
                HashMap<String,Object> requirements = merchant.requirements;
                requirements.forEach((key, value) -> {
                    Object reqValue = requirements.get(key);
                    if (!register.containsKey(key) && reqValue.equals("mandatory")) {
                        apiResponse.setStatus(false);
                        apiResponse.setData(new ErrorResponses(ErrorCode.RESOURCE_NOT_FOUND));
                        apiResponse.setCode(StatusCode.INTERNAL_SERVER_ERROR.code);
                    }else{
                        Object registerValue = register.get(key);
                        if(reqValue.equals("mandatory") && registerValue.equals("")){
                            apiResponse.setStatus(false);
                            apiResponse.setData(new ErrorResponses(ErrorCode.RESOURCE_EMPTY));
                            apiResponse.setCode(StatusCode.INTERNAL_SERVER_ERROR.code);
                        }else{
                            User user = new User();
                            user.user_details = register;
                            userRepository.save(user);
                        }
                    }
                });
            }else {
                apiResponse.setMsg("Authentication Error");
                apiResponse.setStatus(false);
                apiResponse.setData(new ErrorResponses(ErrorCode.INVALID_AUTHENTICATION));
                apiResponse.setCode(StatusCode.FORBIDDEN.code);
            }
        return apiResponse;
    }

   /* @PostMapping("/register")
    private APIResponse getRegisterData(@RequestHeader("Authorization") String token,@RequestBody Register register) throws IOException {
        APIResponse apiResponse = new APIResponse();
        if((token == null || token.isEmpty()) && (register.getMid() == null || register.getMid().isEmpty())){
            throw new InvalidException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        UserRegisterData userRegisterData = userRegisterRepo.findByMid(register.getMid());
        if(userRegisterData!=null &&  userRegisterData.device_token.equals(extractTokenFromHeader(token))){

        }else {
            apiResponse.setMsg("Authentication Error");
            apiResponse.setStatus(false);
            apiResponse.setData(new ErrorResponses(ErrorCode.INVALID_AUTHENTICATION));
            apiResponse.setCode(StatusCode.FORBIDDEN.code);
        }

        return apiResponse;
    }*/
}
