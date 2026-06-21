package com.Kartik.notiflow.Campaign;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import com.Kartik.notiflow.Client.Client;
import com.Kartik.notiflow.Client.ClientRepository;
import com.Kartik.notiflow.Common.Exception.BadRequestException;
import com.Kartik.notiflow.Common.Exception.ConflictException;
import com.Kartik.notiflow.Common.Response.ApiResponseHandler;
import com.Kartik.notiflow.Common.Response.ResponseBuilder;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class CampaignService {
    private final CampaignRepository campaignRepository;
    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;

    public ResponseEntity<ApiResponseHandler<Object>> createCampaign(@RequestBody CampaignDto campaign) {
        try {
            Campaign campaignEntity = modelMapper.map(campaign, Campaign.class);
            Long client = campaign.getClientId();
            Client clientEntity = clientRepository.findById(client).orElseThrow(
                    () -> new RuntimeException("Client not found with id: " + client));
            String isActive = clientEntity.getStatus();
            if (isActive.equals("inactive")) {
                throw new RuntimeException("Cannot create campaign for inactive client");
            }
            campaignEntity.setClient(clientEntity);
            Campaign savedCampaign = campaignRepository.save(campaignEntity);
            CampaignDto campaignDto = modelMapper.map(savedCampaign, CampaignDto.class);
            if (savedCampaign.getClient() != null) {
                campaignDto.setClientId(client);
            }
            ApiResponseHandler<Object> successResponse = ResponseBuilder.success(
                    HttpStatus.CREATED,
                    "Campaign created successfully",
                    campaignDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);

        } catch (ConflictException ex) {
            throw new BadRequestException(
                    "Database error: ConflictException.");
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException(
                    "Database error: Could not save client due to invalid data constraint.");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException(
                    "An unexpected error occurred while processing your request: " + ex.getMessage());
        }
    }

    public ResponseEntity<ApiResponseHandler<Object>> getAllCampaign() {
        try {
            List<Campaign> campaignEntity = campaignRepository.findAll();
            List<CampaignDto> campaignDtos = campaignEntity.stream()
                    .map(campaign -> {
                        CampaignDto dto = modelMapper.map(campaign, CampaignDto.class);
                        if (campaign.getClient() != null) {
                            dto.setClientId(campaign.getClient().getClientId());
                        }
                        return dto;
                    })
                    .toList();
            ApiResponseHandler<Object> successResponse = ResponseBuilder.success(
                    HttpStatus.OK,
                    "All campaigns retrieved successfully",
                    campaignDtos);
            return ResponseEntity.status(HttpStatus.OK).body(successResponse);
        } catch (Exception ex) {
            throw new BadRequestException(
                    "An unexpected error occurred while processing your request: " + ex.getMessage());
        }
    }

    public ResponseEntity<ApiResponseHandler<Object>> getCampaignById(@RequestBody Long CampaignId) {
        try {
            Campaign campaignEntity = campaignRepository.findById(CampaignId).orElseThrow(
                    () -> new BadRequestException("Campaign does not exist with campaign id: " + CampaignId));
            CampaignDto campaignDto = modelMapper.map(campaignEntity, CampaignDto.class);
            if (campaignDto.getClientId() == null) {
                campaignDto.setClientId(campaignEntity.getClient().getClientId());
            }
            ApiResponseHandler<Object> successResponse = ResponseBuilder.success(
                    HttpStatus.OK,
                    "Campaign retrieved successfully",
                    campaignDto);
            return ResponseEntity.status(HttpStatus.OK).body(successResponse);
        } catch (Exception ex) {
            throw new BadRequestException(
                    "An unexpected error occurred while processing your request: " + ex.getMessage());
        }
    }
}
