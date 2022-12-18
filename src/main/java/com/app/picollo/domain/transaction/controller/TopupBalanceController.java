package com.app.picollo.domain.transaction.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.picollo.domain.transaction.dto.TopupBalanceRequest;
import com.app.picollo.domain.transaction.service.TopupBalanceService;
import com.app.picollo.infrastructure.constant.APIConstant;
import com.app.picollo.infrastructure.model.BaseResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class TopupBalanceController {

    @Autowired
    TopupBalanceService topupService;

    @PostMapping(value = APIConstant.BALANCE_TOPUP_PATH)
    public ResponseEntity<BaseResponse> balanceTopup(@RequestBody TopupBalanceRequest topupBalanceRequest,  HttpServletRequest request) {
    
        BaseResponse baseResponse;
        String username = (String) request.getAttribute(APIConstant.USERNAME_ATTR);
        try {
            baseResponse = topupService.balanceTopup(topupBalanceRequest, username);
        } catch (Exception e) {
            baseResponse = new BaseResponse().failedProcess(e.getMessage(), null);
        }

        return ResponseEntity.status(baseResponse.getStatus()).body(baseResponse);
    }

    @GetMapping(value = APIConstant.BALANCE_READ_PATH)
    public ResponseEntity<BaseResponse> balanceRead(HttpServletRequest request) {

        BaseResponse baseResponse;
        String username = (String) request.getAttribute(APIConstant.USERNAME_ATTR);
        try {
            baseResponse = topupService.checkBalance(username);
        } catch (Exception e) {
            baseResponse = new BaseResponse().failedProcess(e.getMessage(), null);
        }

        return ResponseEntity.status(baseResponse.getStatus()).body(baseResponse);
    }

    
}
