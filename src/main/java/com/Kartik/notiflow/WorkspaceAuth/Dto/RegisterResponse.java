package com.Kartik.notiflow.WorkspaceAuth.Dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

    private Long workspaceId;
    private String workspaceName;
    private String username;
    private Boolean active;
    private LocalDateTime createdAt;
    private String Api;
}