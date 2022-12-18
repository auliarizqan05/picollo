package com.app.picollo.advice;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.app.picollo.domain.user.entity.User;
import com.app.picollo.domain.user.service.UserService;
import com.app.picollo.infrastructure.constant.APIConstant;
import com.app.picollo.infrastructure.model.BaseResponse;
import com.app.picollo.infrastructure.util.JsonUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SecurityAdvice implements Filter {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";

    @Autowired
    UserService userService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String url = httpServletRequest.getServletPath();

        boolean staticResourcesAllowed = url.contains("documentation") || url.contains("h2-console") || url.contains(APIConstant.CREATE_USER_PATH);
        if (!staticResourcesAllowed) {

            String extractAuthHeader = httpServletRequest.getHeader(AUTHORIZATION) == null ? "" : httpServletRequest.getHeader(AUTHORIZATION);
            User user = userService.verifyToken(extractAuthHeader.replace(BEARER, ""));

            if (Objects.isNull(user)) {
                log.error("AUTHORIZATION header not initiate");
                BaseResponse baseResponse = new BaseResponse().failedProcess(HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(), "User token not valid");
                String responseJson = JsonUtil.objectToJson(baseResponse);

                httpServletResponse.setStatus(baseResponse.getStatus());
                httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
                httpServletResponse.getWriter().write(responseJson);

                httpServletResponse.getWriter().flush();
                httpServletResponse.getWriter().close();
            } else {
                httpServletRequest.setAttribute("username", user.getUsername());
            }
        }

        chain.doFilter(httpServletRequest, httpServletResponse);
    }

}
