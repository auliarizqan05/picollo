package com.app.picollo.domain.transaction.service;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.picollo.domain.transaction.dto.BalanceResponse;
import com.app.picollo.domain.transaction.dto.TopupBalanceRequest;
import com.app.picollo.domain.transaction.entity.Transaction;
import com.app.picollo.domain.transaction.entity.Balance;
import com.app.picollo.domain.transaction.repository.TransactionRepository;
import com.app.picollo.domain.transaction.repository.BalanceRepository;
import com.app.picollo.infrastructure.constant.TransactionConstant;
import com.app.picollo.infrastructure.model.BaseResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TopupBalanceService {

    final BalanceRepository balanceRepository;
    final TransactionRepository transactionRepository;

    @Transactional
    public BaseResponse balanceTopup(TopupBalanceRequest request, String username) {

        if (request.getAmount().compareTo(BigDecimal.ZERO) < 0
                || request.getAmount().compareTo(BigDecimal.valueOf(10000000)) > 0) {
            log.debug("Amount is invalid");
            return new BaseResponse().failedProcess(HttpStatus.BAD_REQUEST.value(), "Invalid topup amount");
        }

        Balance userBalance = balanceRepository.findByUsername(username).map(ubalance -> {
            BigDecimal calculate = request.getAmount().add(ubalance.getBalance());
            ubalance.setBalance(calculate);

            return ubalance;
        }).orElse(Balance.builder()
                .balance(request.getAmount())
                .username(username)
                .build());

        balanceRepository.save(userBalance);

        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .type(TransactionConstant.TOPUP)
                .username(username)
                .build();
        transactionRepository.save(transaction);

        return new BaseResponse().successProcess(HttpStatus.NO_CONTENT, "Topup Successful");
    }

    public BaseResponse checkBalance(String username) {

        log.info("Check Balance");

        Balance userBalance = balanceRepository.findByUsername(username).orElseThrow();
        BalanceResponse balanceResponse = BalanceResponse.builder().balance(userBalance.getBalance()).build();

        return new BaseResponse().successProcess(balanceResponse);
    }
}
