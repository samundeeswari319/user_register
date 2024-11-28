package com.merchant.register.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merchant.register.common.APIResponse;
import com.merchant.register.enumclass.ErrorCode;
import com.merchant.register.enumclass.StatusCode;
import com.merchant.register.model.Merchant;
import com.merchant.register.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class MerchantService {

    @Autowired
    private MerchantRepository merchantRepository;



    public APIResponse updateSelectedFields(String id, String jsonFilepath) {
        APIResponse apiResponse = new APIResponse();

        try {
            Map<String, Object> jsonData = loadJsonDataFromFile(jsonFilepath);
            if (jsonData == null) {
                apiResponse.setStatus(false);
                apiResponse.setMsg("Failed to read JSON file.");
                return apiResponse;
            }

            Merchant merchant = merchantRepository.findByMid(id);
            if (merchant == null) {
                apiResponse.setStatus(false);
                apiResponse.setMsg(String.valueOf(ErrorCode.RESOURCE_EMPTY));
                return apiResponse;
            }

            merchant.setJson_requirements(new ObjectMapper().writeValueAsString(jsonData));
            merchantRepository.save(merchant);

            apiResponse.setStatus(true);
            apiResponse.setMsg("Merchant requirements updated successfully.");
        } catch (Exception e) {
            apiResponse.setStatus(false);
            apiResponse.setMsg("An error occurred: " + e.getMessage());
        }

        return apiResponse;
    }
    public APIResponse updateMerchant(String mid, Merchant updatedModel) {
        APIResponse apiResponse = new APIResponse();

        try {
            Merchant existingModel = merchantRepository.findByMid(mid);
            if (existingModel == null) {
                apiResponse.setStatus(false);
                apiResponse.setMsg("Merchant not found for the given ID.");
                return apiResponse;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> existingJsonData = objectMapper.readValue(existingModel.getJson_requirements(), Map.class);

            Map<String, Object> updatedJsonData = objectMapper.readValue(updatedModel.getJson_requirements(), Map.class);

            existingJsonData.putAll(updatedJsonData);

            String updatedJsonString = objectMapper.writeValueAsString(existingJsonData);


            existingModel.setJson_requirements(updatedJsonString);

            Merchant savedModel = merchantRepository.save(existingModel);

            apiResponse.setStatus(true);
            apiResponse.setCode(200);
            apiResponse.setData(savedModel);
            apiResponse.setError(null);
            apiResponse.setMsg("Merchant updated successfully.");
        } catch (Exception e) {
            apiResponse.setStatus(false);
            apiResponse.setCode(500);
            apiResponse.setData(null);
            apiResponse.setError("INTERNAL_SERVER_ERROR");
            apiResponse.setMsg("An error occurred while updating the merchant: " + e.getMessage());
        }

        return apiResponse;
    }
    public APIResponse deleteMerchantKey(String mid, String key) {
        APIResponse response = new APIResponse();

        try {
            Merchant merchant = merchantRepository.findByMid(mid);

            if (merchant != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> jsonRequirements = objectMapper.readValue(merchant.getJson_requirements(), Map.class);

                if (jsonRequirements.containsKey(key)) {
                    jsonRequirements.remove(key);
                    merchant.setJson_requirements(objectMapper.writeValueAsString(jsonRequirements));
                    merchantRepository.save(merchant);

                    response.setCode(StatusCode.SUCCESS.code);
                    response.setStatus(true);
                    response.setMsg("Key '" + key + "' deleted successfully from merchant.");
                } else {
                    response.setCode(StatusCode.FAILURE.code);
                    response.setStatus(false);
                    response.setMsg("Key '" + key + "' not found in merchant json_requirements.");
                }
            } else {
                response.setCode(StatusCode.FAILURE.code);
                response.setStatus(false);
                response.setMsg("Merchant not found for mid: " + mid);
            }
        } catch (Exception e) {
            // Handle unexpected errors
            response.setCode(StatusCode.FAILURE.code);
            response.setStatus(false);
            response.setMsg("An error occurred while deleting the key: " + e.getMessage());
        }

        return response;
    }


    private Map<String, Object> loadJsonDataFromFile(String filePath) {
        try {
            File file = new File(filePath);
            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readValue(file, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
