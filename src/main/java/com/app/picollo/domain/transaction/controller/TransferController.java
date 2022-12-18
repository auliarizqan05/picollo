package com.app.picollo.domain.transaction.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.picollo.domain.transaction.dto.TransferRequest;
import com.app.picollo.domain.transaction.service.TransferService;
import com.app.picollo.infrastructure.constant.APIConstant;
import com.app.picollo.infrastructure.model.BaseResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class TransferController {

    @Autowired
    TransferService transferService;

    @PostMapping(value = APIConstant.TRANSFER_PATH)
    public ResponseEntity<BaseResponse> balanceTopup(@RequestBody TransferRequest transferRequest,  HttpServletRequest request) {
    
        BaseResponse baseResponse;
        String username = (String) request.getAttribute(APIConstant.USERNAME_ATTR);
        try {
            baseResponse = transferService.transfer(transferRequest, username);
        } catch (Exception e) {
            baseResponse = new BaseResponse().failedProcess(e.getMessage(), null);
        }

        return ResponseEntity.status(baseResponse.getStatus()).body(baseResponse);
    }

    @GetMapping(value = APIConstant.TOP_USER_PATH)
    public ResponseEntity<BaseResponse> topUsers(HttpServletRequest request) {
    
        BaseResponse baseResponse;
        try {
            baseResponse = transferService.getTopUsers();
        } catch (Exception e) {
            baseResponse = new BaseResponse().failedProcess(e.getMessage(), null);
        }

        return ResponseEntity.status(baseResponse.getStatus()).body(baseResponse);
    }

    @GetMapping(value = APIConstant.TOP_TRANSACTION_PER_USER_PATH)
    public ResponseEntity<BaseResponse> topTransactionPerUser(HttpServletRequest request) {
    
        BaseResponse baseResponse;
        String username = (String) request.getAttribute(APIConstant.USERNAME_ATTR);
        try {
            baseResponse = transferService.getTopTransactionPerUser(username);
        } catch (Exception e) {
            baseResponse = new BaseResponse().failedProcess(e.getMessage(), null);
        }

        return ResponseEntity.status(baseResponse.getStatus()).body(baseResponse);
    }

    
}
