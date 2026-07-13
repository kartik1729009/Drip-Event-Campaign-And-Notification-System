package com.Kartik.notiflow.UserAuth.Dto;

import com.Kartik.notiflow.Enum.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleRequest {
    private Role role;
}
