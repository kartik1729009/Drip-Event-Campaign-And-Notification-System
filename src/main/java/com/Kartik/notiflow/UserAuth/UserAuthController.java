package com.Kartik.notiflow.UserAuth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Kartik.notiflow.Common.Response.ApiResponseHandler;
import com.Kartik.notiflow.UserAuth.Dto.LoginUserRequest;
import com.Kartik.notiflow.UserAuth.Dto.RegisterUserRequest;
import com.Kartik.notiflow.UserAuth.Dto.UpdateRoleRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/userAuth")
@RequiredArgsConstructor
public class UserAuthController {

    private final UserAuthService userAuthService;

    // workspace JWT auth — creates first admin, blocked if workspace already has users
    @PostMapping("/registerAdmin")
    public ResponseEntity<ApiResponseHandler<Object>> registerAdmin(
            @RequestBody RegisterUserRequest request) {
        return userAuthService.registerAdmin(request);
    }

    // user JWT auth — admin creates a new user with a role
    @PostMapping("/register")
    public ResponseEntity<ApiResponseHandler<Object>> registerUser(
            @RequestBody RegisterUserRequest request) {
        return userAuthService.registerUser(request);
    }

    // no auth — workspace resolved from body, login blocked if user inactive
    @PostMapping("/login")
    public ResponseEntity<ApiResponseHandler<Object>> loginUser(
            @RequestBody LoginUserRequest request) {
        return userAuthService.loginUser(request);
    }

    // user JWT auth — admin updates role of a user
    @PatchMapping("/updateRole/{userId}")
    public ResponseEntity<ApiResponseHandler<Object>> updateRole(
            @PathVariable Long userId,
            @RequestBody UpdateRoleRequest request) {
        return userAuthService.updateRole(userId, request);
    }

    // user JWT auth — admin toggles active/inactive status of a user
    @PatchMapping("/toggleStatus/{userId}")
    public ResponseEntity<ApiResponseHandler<Object>> toggleStatus(
            @PathVariable Long userId) {
        return userAuthService.toggleStatus(userId);
    }

    // user JWT auth — admin gets all users in their workspace
    @GetMapping("/getAllUsers")
    public ResponseEntity<ApiResponseHandler<Object>> getAllUsers() {
        return userAuthService.getAllUsers();
    }
}
