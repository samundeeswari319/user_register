package com.merchant.register.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merchant.register.common.APIResponse;
import com.merchant.register.common.ErrorResponses;
import com.merchant.register.common.InvalidException;
import com.merchant.register.config.JwtUtils;
import com.merchant.register.enumclass.ErrorCode;
import com.merchant.register.enumclass.StatusCode;
import com.merchant.register.model.*;
import com.merchant.register.repository.MerchantRepository;
import com.merchant.register.repository.UserRegisterRepo;
import com.merchant.register.repository.UserRepository;
import com.merchant.register.services.MerchantService;
import com.merchant.register.services.SequenceGeneratorService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

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

    @Autowired
    private MerchantService merchantService;

    @PostMapping("/login/auth/create_merchant")
    public APIResponse createMerchant(@RequestBody Merchant model) {
        String authToken;
        APIResponse response = new APIResponse();

        try {
            if(model.getApp_id() == null || model.getApp_name() == null){
                response.setStatus(false);
                response.setData(new ErrorResponses(ErrorCode.RESOURCE_NOT_FOUND));
                response.setCode(StatusCode.INTERNAL_SERVER_ERROR.code);
                return response;
            }
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
            model.setJson_requirements(null);
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



    @PostMapping("/edit_merchant")
    public APIResponse updateMerchant(@RequestBody Map<String, Object> updatedModel) throws JsonProcessingException {
        Object jsonRequirements = updatedModel.get("json_requirements");
    String jsonRequirementsString = new ObjectMapper().writeValueAsString(jsonRequirements);
    Merchant merchant = new Merchant();
    merchant.setJson_requirements(jsonRequirementsString);
    String userId = authTokenModel.getUser_id();
    if (userId == null || userId.trim().isEmpty()) {
        APIResponse response = new APIResponse();
        response.setStatus(false);
        response.setCode(400);
        response.setMsg(ErrorCode.INVALID_USERID.code);
        response.setError(null);
        return response;
    }

    return merchantService.updateMerchant(userId, merchant);
    }


    @PostMapping("/delete_merchant")
    public ResponseEntity<APIResponse> deleteMerchantKey( @RequestParam String key) {
        String userId = authTokenModel.getUser_id();
        if (userId == null || userId.trim().isEmpty()) {
            APIResponse response = new APIResponse();
            response.setStatus(false);
            response.setCode(400);
            response.setMsg(ErrorCode.INVALID_USERID.code);
            response.setError(null);
            return ResponseEntity.badRequest().body(response);
        }
        APIResponse response = merchantService.deleteMerchantKey(userId, key);
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
    public APIResponse updateSelectedFields(
            @RequestParam("jsonFilepath") MultipartFile file) {
        String userId = authTokenModel.getUser_id();
        if (userId == null || userId.trim().isEmpty()) {
            APIResponse response = new APIResponse();
            response.setStatus(false);
            response.setCode(400);
            response.setMsg(ErrorCode.INVALID_USERID.code);
            response.setError("INVALID_USER_ID");
            return response;
        }
        return merchantService.updateSelectedFields(userId, file);
    }

    /*@PostMapping("/register")
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
    }*/

    @PostMapping("/register_user")
    private APIResponse userRegister(@RequestBody HashMap<String,Object> register) throws IOException {
        APIResponse apiResponse = new APIResponse();
        //String midL = register.get("mid").toString();
        List<String> errors = new ArrayList<>();
        Merchant merchant = merchantRepository.findByMid(authTokenModel.getUser_id());
        if(merchant != null){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                // Convert JSON string to Map
                Map<String, Object> requirements = objectMapper.readValue(merchant.json_requirements, new TypeReference<Map<String, Object>>() {
                });

                requirements.forEach((key, value) -> {
                    if(register.containsKey(key)){
                        Object nameObject = requirements.get(key);
                        // Check if it's a Map and cast
                        if (nameObject instanceof Map) {
                            Map<String, Object> nameMap = (Map<String, Object>) nameObject;
                            boolean isMandatory = (boolean) nameMap.get("is_mandatory");
                            if(isMandatory && register.get(key).equals("")){
                                errors.add(ErrorCode.RESOURCE_NOT_FOUND.message);
                            }
                        }
                    }else{
                        errors.add(ErrorCode.RESOURCE_NOT_FOUND.message);
                    }
                });

                if(errors.isEmpty()){
                    long id = sequenceGeneratorService.generateSequence(Merchant.SEQUENCE_NAME);
                    User user = new User();
                    user.setId(id);
                    user.user_details = register;
                    userRepository.save(user);
                    apiResponse.setStatus(true);
                    apiResponse.setData(user);
                    apiResponse.setCode(StatusCode.SUCCESS.code);
                }else{
                    apiResponse.setStatus(false);
                    apiResponse.setData(new ErrorResponses(ErrorCode.RESOURCE_NOT_FOUND));
                    apiResponse.setCode(StatusCode.INTERNAL_SERVER_ERROR.code);
                }
            }catch (Exception e){
                apiResponse.setMsg(e.getMessage());
                apiResponse.setStatus(false);
                apiResponse.setData(errors);
                apiResponse.setCode(StatusCode.INTERNAL_SERVER_ERROR.code);
            }
            }else{
                apiResponse.setMsg("Merchant Not found");
                apiResponse.setStatus(false);
                apiResponse.setData(new ErrorResponses(ErrorCode.INVALID_AUTHENTICATION));
                apiResponse.setCode(StatusCode.FORBIDDEN.code);
            }
        return apiResponse;
    }
}
