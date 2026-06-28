package com.Kartik.notiflow.WorkspaceAuth.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {
    private String username;
    public String workspaceName;
    private String password;
}
