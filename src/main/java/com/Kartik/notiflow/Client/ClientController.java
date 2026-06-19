package com.Kartik.notiflow.Client;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/client")
public class ClientController {
    private final ClientService clientService;

    @PostMapping("/createClinet")
    public ClientDto createClient(ClientDto client){
        return clientService.createClient(client);
    }
}
