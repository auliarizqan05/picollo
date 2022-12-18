package com.app.picollo.domain.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.picollo.domain.user.dto.UserRequest;
import com.app.picollo.domain.user.service.UserService;
import com.app.picollo.infrastructure.constant.APIConstant;
import com.app.picollo.infrastructure.model.BaseResponse;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping(value = APIConstant.CREATE_USER_PATH)
    public ResponseEntity<BaseResponse> createUser(@RequestBody UserRequest userRequest) {

        BaseResponse baseResponse;
        try {
            baseResponse = userService.createUser(userRequest);
        } catch (Exception e) {
            baseResponse = new BaseResponse().failedProcess(e.getMessage(), null);
        }

        return ResponseEntity.status(baseResponse.getStatus()).body(baseResponse);
    }
    
}
