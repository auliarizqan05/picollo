package com.app.picollo.domain.transaction.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.picollo.domain.transaction.dto.TopTransactionPerUserResponse;
import com.app.picollo.domain.transaction.dto.TopUserResponse;
import com.app.picollo.domain.transaction.dto.TransferRequest;
import com.app.picollo.domain.transaction.entity.Balance;
import com.app.picollo.domain.transaction.entity.Transaction;
import com.app.picollo.domain.transaction.repository.BalanceRepository;
import com.app.picollo.domain.transaction.repository.TransactionRepository;
import com.app.picollo.domain.user.repository.UserRepository;
import com.app.picollo.infrastructure.constant.TransactionConstant;
import com.app.picollo.infrastructure.model.BaseResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransferService {

    final UserRepository userRepository;
    final BalanceRepository balanceRepository;
    final TransactionRepository transactionRepository;

    @Transactional
    public BaseResponse transfer(TransferRequest request, String username) {

        log.info("Transfer amount from user {}", username);

        String usernameDestination = request.getToUsername();

        if (username.equalsIgnoreCase(usernameDestination)){
            log.info("user cannot did a transfer to same source user");
            return new BaseResponse().failedProcess(HttpStatus.FORBIDDEN.value(), "Destination user same with source user");
        }

        // check user transfer destination, the account exist or not
        boolean isUserDestinationNotExist = userRepository.findByUsername(request.getToUsername()).isEmpty();
        if (isUserDestinationNotExist) {
            log.info("The destination user is not exist");
            return new BaseResponse().failedProcess(HttpStatus.NOT_FOUND.value(), "Destination user not found");
        }

        Balance userBalance = balanceRepository.findByUsername(username).map(ubalance -> {
            if (ubalance.getBalance().compareTo(request.getAmount()) < 0) {
                return null;
            }
            BigDecimal calculate = ubalance.getBalance().subtract(request.getAmount());
            ubalance.setBalance(calculate);

            return ubalance;
        }).orElse(null);

        if (userBalance == null) {
            log.info("The balance not sufficient to do a transfer");
            return new BaseResponse().failedProcess(HttpStatus.BAD_REQUEST.value(), "Insufficient balance");
        }

        Balance userBalanceDestination = balanceRepository.findByUsername(usernameDestination).map(ubalance -> {
            BigDecimal calculate = ubalance.getBalance().add(request.getAmount());
            ubalance.setBalance(calculate);

            return ubalance;
        }).orElseThrow(() -> new IllegalArgumentException("Cannot find user destination balance"));

        balanceRepository.save(userBalance);
        balanceRepository.save(userBalanceDestination);

        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .type(TransactionConstant.TRANSFER)
                .username(username)
                .toUsername(usernameDestination)
                .build();
        transactionRepository.save(transaction);

        return new BaseResponse().successProcess(HttpStatus.NO_CONTENT, "Transfer Success");
    }

    public BaseResponse getTopUsers() {

        log.info("Query top users transfer transaction");

        // query all transaction transfer
        List<TopUserResponse> transactionList = transactionRepository.findByType(TransactionConstant.TRANSFER);
        log.info("list transaction = {}", transactionList );

        return new BaseResponse().successProcess(transactionList);
    }

    public BaseResponse getTopTransactionPerUser(String username) {

        log.info("Query top transaction per user");

        List<TopTransactionPerUserResponse> transactionList = transactionRepository.findByTypeAndUsernameOrToUsername(
                TransactionConstant.TRANSFER, username, username,
                PageRequest.of(0, 10, Sort.Direction.DESC, "amount"))
                .stream()
                .map(trx -> {
                    BigDecimal amount = trx.getAmount();
                    String uname = trx.getToUsername();
                    if(username.equalsIgnoreCase(trx.getUsername())){
                        amount = amount.negate();
                        uname = trx.getUsername();
                    }
                    return TopTransactionPerUserResponse
                            .builder()
                            .username(uname)
                            .amount(amount)
                            .build();

                }).collect(Collectors.toList());

        return new BaseResponse().successProcess(transactionList);
    }
}
