package com.Kartik.notiflow.UserAuth.Dto;


import com.Kartik.notiflow.Enum.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class RegisterUserRequest {
    private String userName;
    private String email;
    private String password;
    private Role role;
    private Long workspaceId;
}