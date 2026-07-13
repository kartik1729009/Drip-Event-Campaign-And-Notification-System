package com.Kartik.notiflow.Notification.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recipient {
    private String email;
    private String phone;
    private String whatsapp;
    private String deviceToken;
    private String userId;
    private String discordId;
    private String telegramId;
}
