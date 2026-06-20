package com.Kartik.notiflow.Client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Kartik.notiflow.Common.Response.ApiResponseHandler;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/client")

public class ClientController {
    private final ClientService clientService;

    @PostMapping("/createClient")
    public ResponseEntity<ApiResponseHandler<Object>> createClient(@RequestBody ClientDto client) {
        System.out.println("calling create client service");
        return clientService.createClient(client);
    }

    @GetMapping("/getAllClients")
    public ResponseEntity<ApiResponseHandler<Object>> getAllClient() {
        return clientService.getAllClient();
    }

    @GetMapping("/getClientById/{clientId}")
    public ResponseEntity<ApiResponseHandler<Object>> getClientById(@PathVariable long clientId) {
        return clientService.getClientById(clientId);
    }
}
