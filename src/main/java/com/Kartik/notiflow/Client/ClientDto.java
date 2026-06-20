package com.Kartik.notiflow.Client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ClientDto {
    private Long clientId;
    private String name;
    private String userName;
    private String workspace;
    private String apiKey;
    private String status;
    private String callbackUrl;
}
