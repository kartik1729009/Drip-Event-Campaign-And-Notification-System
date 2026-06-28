package com.Kartik.notiflow.WorkspaceAuth.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {
    private String workspaceName;
    private String username;
    private String password;
    private String callbackUrl;
    private String Api;
}
