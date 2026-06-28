package com.Kartik.notiflow.UserAuth.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class LoginUserRequest {
    private String workspaceName;
    private String email;
    private String password;
}