package com.Kartik.notiflow.WorkspaceAuth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.Kartik.notiflow.Common.Response.ApiResponseHandler;
import com.Kartik.notiflow.WorkspaceAuth.Dto.LoginRequest;
import com.Kartik.notiflow.WorkspaceAuth.Dto.RegisterRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/workspaceAuth")
@RequiredArgsConstructor
public class WorkspaceAuthController {
    private final WorkspaceAuthService workspaceAuthService;
    @PostMapping("/createWorkspace")
    public ResponseEntity<ApiResponseHandler<Object>>createWorspace(@RequestBody RegisterRequest registerRequest){
        return workspaceAuthService.createWorkspace(registerRequest);
    }
    @PostMapping("/loginWorkspace")
    public ResponseEntity<ApiResponseHandler<Object>> loginWorkspace(@RequestBody LoginRequest loginRequest){
        return workspaceAuthService.loginWorkspace(loginRequest);
    }
}
