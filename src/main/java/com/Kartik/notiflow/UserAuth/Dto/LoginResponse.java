package com.Kartik.notiflow.UserAuth.Dto;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
}
