package com.app.picollo.domain.user.service;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.picollo.domain.transaction.entity.Balance;
import com.app.picollo.domain.transaction.repository.BalanceRepository;
import com.app.picollo.domain.user.dto.UserRequest;
import com.app.picollo.domain.user.dto.UserResponse;
import com.app.picollo.domain.user.entity.User;
import com.app.picollo.domain.user.repository.UserRepository;
import com.app.picollo.infrastructure.model.BaseResponse;
import com.app.picollo.infrastructure.security.JWTHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    final UserRepository userRepository;
    final BalanceRepository userBalanceRepository;

    @Transactional
    public BaseResponse createUser(UserRequest userRequest) {

        log.info("Create User Service");

        boolean isUserExist = userRepository.findByUsername(userRequest.getUsername()).isPresent();
        if (isUserExist) {
            log.debug("User is exist");
            return new BaseResponse().failedProcess(HttpStatus.CONFLICT.value(), "Username already exists");
        }

        String token = JWTHelper.generateToken(userRequest);
        log.info("token {}", token);

        User user = User.builder()
                .username(userRequest.getUsername())
                .token(token)
                .build();
        userRepository.save(user);

        Balance userBalance = Balance.builder()
                .username(userRequest.getUsername())
                .balance(BigDecimal.ZERO)
                .build();
        userBalanceRepository.save(userBalance);

        UserResponse userResponse = UserResponse.builder().token(user.getToken()).build();
        return new BaseResponse().successProcess(userResponse);
    }

    public User verifyToken(String token) {

        log.info("Verify User Token {}" , token);
        return userRepository.findByToken(token).orElse(null);
    }

    public User getUser(String username) {

        log.info("Get User {}" , username);
        return userRepository.findByUsername(username).orElse(null);
    }

}
