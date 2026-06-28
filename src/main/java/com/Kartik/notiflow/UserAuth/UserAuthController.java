package com.Kartik.notiflow.UserAuth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Kartik.notiflow.Common.Response.ApiResponseHandler;
import com.Kartik.notiflow.UserAuth.Dto.LoginUserRequest;
import com.Kartik.notiflow.UserAuth.Dto.RegisterUserRequest;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/userAuth")
@RequiredArgsConstructor
public class UserAuthController {

    private final UserAuthService userAuthService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseHandler<Object>> registerUser(
            @RequestBody RegisterUserRequest request) {

        return userAuthService.registerUser(request);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseHandler<Object>> loginUser(
            @RequestBody LoginUserRequest request,
            HttpServletResponse response) {

        return userAuthService.loginUser(request);
    }
}