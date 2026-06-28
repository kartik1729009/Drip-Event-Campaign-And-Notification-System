package com.Kartik.notiflow.UserAuth.Dto;

import java.time.LocalDateTime;

import com.Kartik.notiflow.Enum.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterUserResponse {
    private Long userAuthId;
    private String userName;
    private String email;
    private Role role;
    private Boolean active;
    private Long workspaceId;
    private LocalDateTime createdAt;
}