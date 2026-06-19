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
    private long clientId;
    private String name;
    private String workspace;
    private String apiKey;
    private String callbackUrl;
}
