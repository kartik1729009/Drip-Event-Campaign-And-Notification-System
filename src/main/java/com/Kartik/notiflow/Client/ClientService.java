package com.Kartik.notiflow.Client;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.Kartik.notiflow.Common.Exception.ConflictException;
import com.Kartik.notiflow.Common.Exception.BadRequestException;
import com.Kartik.notiflow.Common.Response.ApiResponseHandler;
import com.Kartik.notiflow.Common.Response.ResponseBuilder;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;

    public ResponseEntity<ApiResponseHandler<Object>> createClient(ClientDto client) {
        try {
            System.out.println("entered try block");
            Boolean exist = clientRepository.findByUserName(client.getUserName()).isPresent();
            if (exist) {
                throw new ConflictException(
                        "Client already exists with the user name: " + client.getUserName());
            }
            Client clientEntity = modelMapper.map(client, Client.class);
            Client savedClient = clientRepository.save(clientEntity);
            ClientDto savedDto = modelMapper.map(savedClient, ClientDto.class);
            ApiResponseHandler<Object> successResponse = ResponseBuilder.success(
                    HttpStatus.CREATED,
                    "Client created successfully",
                    savedDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
        } 
        catch (ConflictException ex) {
            throw new BadRequestException(
                    "Database error: ConflictException.");
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException(
                    "Database error: Could not save client due to invalid data constraint.");
        } 
        catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException(
                    "An unexpected error occurred while processing your request: " + ex.getMessage());
        }
    }

    public ResponseEntity<ApiResponseHandler<Object>> getAllClient() {
        try {
            List<Client> allClient = clientRepository.findAll();
            List<ClientDto> allClientDtos = allClient.stream()
                    .map(client -> modelMapper.map(client, ClientDto.class))
                    .toList();
            ApiResponseHandler<Object> successResponse = ResponseBuilder.success(
                    HttpStatus.OK,
                    "All clients retrieved successfully",
                    allClientDtos);
            return ResponseEntity.status(HttpStatus.OK).body(successResponse);
        } catch (Exception ex) {
            throw new BadRequestException(
                    "An unexpected error occurred while processing your request: " + ex.getMessage());
        }
    }

    public ResponseEntity<ApiResponseHandler<Object>> getClientById(long clientId) {
        try {
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new BadRequestException(
                            "Client does not exist with client id: " + clientId));

            ClientDto clientDto = modelMapper.map(client, ClientDto.class);
            ApiResponseHandler<Object> successResponseHandler = ResponseBuilder.success(
                    HttpStatus.OK,
                    "Client retrieved successfully",
                    clientDto);

            return ResponseEntity.status(HttpStatus.OK).body(successResponseHandler);
        } catch (Exception ex) {
            throw new BadRequestException(
                    "An unexpected error occurred while processing your request: " + ex.getMessage());
        }
    }
}