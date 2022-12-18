package com.app.picollo.infrastructure.advice;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.app.picollo.infrastructure.model.BaseResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class ResponseErrorAdvice {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse> handleMethodArgumentNotValid(Exception ex,
                                                                     HttpServletRequest request) {
        log.error("request {} ", request.getQueryString());
        BaseResponse response = new BaseResponse(HttpStatus.BAD_REQUEST.value(), 1,
            HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<BaseResponse> handleConversionFailedException(Exception ex,
                                                                        HttpServletRequest request) {

        log.error("request {} ", request.getQueryString());
        BaseResponse response = new BaseResponse(HttpStatus.BAD_REQUEST.value(), 1,
                ex.getLocalizedMessage(), ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


}
