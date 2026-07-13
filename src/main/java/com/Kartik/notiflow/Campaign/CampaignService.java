package com.Kartik.notiflow.Campaign;

import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.Kartik.notiflow.Campaign.Dto.CampaignRequestDto;
import com.Kartik.notiflow.Campaign.Dto.CampaignResponseDto;
import com.Kartik.notiflow.Common.Exception.BadRequestException;
import com.Kartik.notiflow.Common.Exception.ResourceNotFoundException;
import com.Kartik.notiflow.Common.Response.ApiResponseHandler;
import com.Kartik.notiflow.Common.Response.ResponseBuilder;
import com.Kartik.notiflow.Enum.DefinitionStatus;
import com.Kartik.notiflow.Enum.Role;
import com.Kartik.notiflow.Security.CurrentAuth;
import com.Kartik.notiflow.UserAuth.UserAuth;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuth;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final ModelMapper modelMapper;
    private final CurrentAuth currentAuth;

    // helper: map entity -> response dto
    private CampaignResponseDto toResponseDto(Campaign campaign) {
        CampaignResponseDto dto = modelMapper.map(campaign, CampaignResponseDto.class);
        dto.setWorkspaceName(campaign.getWorkspace().getWorkspaceName());
        dto.setCreatedByUserId(
                campaign.getCreatedBy() != null ? campaign.getCreatedBy().getUserAuthId() : null);
        return dto;
    }

    // create campaign — user auth, workspace resolved from user
    public ResponseEntity<ApiResponseHandler<Object>> createCampaign(CampaignRequestDto request) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = user.getWorkspace();
            if(user.getRole().equals(Role.ADMIN)){
                throw new BadRequestException("Admin cannot create campaign");
            }
            Campaign campaign = new Campaign();
            campaign.setWorkspace(workspace);
            campaign.setCreatedBy(user);
            campaign.setName(request.getName());
            campaign.setDescription(request.getDescription());
            campaign.setStartDate(request.getStartDate());
            campaign.setStatus(DefinitionStatus.INACTIVE);

            Campaign saved = campaignRepository.save(campaign);

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.CREATED, "Campaign created successfully.", toResponseDto(saved));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Database error: invalid data constraint.");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    // get all campaigns in workspace — user auth, workspace resolved from user
    public ResponseEntity<ApiResponseHandler<Object>> getAllCampaign() {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = user.getWorkspace();

            List<Campaign> campaigns = campaignRepository.findByWorkspace(workspace);
            List<CampaignResponseDto> dtos = campaigns.stream().map(this::toResponseDto).toList();

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "Campaigns retrieved successfully.", dtos);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    // get campaign by id — user auth, checks workspace ownership
    public ResponseEntity<ApiResponseHandler<Object>> getCampaignById(Long campaignId) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = user.getWorkspace();

            Campaign campaign = campaignRepository.findById(campaignId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Campaign not found with id: " + campaignId));

            if (!campaign.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("Campaign does not belong to your workspace.");
            }

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "Campaign retrieved successfully.", toResponseDto(campaign));
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    // update campaign name/description/startDate — user auth, any role
    public ResponseEntity<ApiResponseHandler<Object>> patchCampaign(
            Long campaignId, CampaignRequestDto request) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = user.getWorkspace();

            Campaign campaign = campaignRepository.findById(campaignId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Campaign not found with id: " + campaignId));

            if (!campaign.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("Campaign does not belong to your workspace.");
            }

            Optional.ofNullable(request.getName()).ifPresent(campaign::setName);
            Optional.ofNullable(request.getDescription()).ifPresent(campaign::setDescription);
            Optional.ofNullable(request.getStartDate()).ifPresent(campaign::setStartDate);

            Campaign saved = campaignRepository.save(campaign);

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "Campaign updated successfully.", toResponseDto(saved));
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Database error: invalid data constraint.");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    // toggle campaign status ACTIVE <-> INACTIVE — MANAGER only
    public ResponseEntity<ApiResponseHandler<Object>> toggleStatus(Long campaignId) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = user.getWorkspace();

            if (!user.getRole().equals(Role.MANAGER)) {
                throw new BadRequestException("Only a Manager can change campaign status.");
            }

            Campaign campaign = campaignRepository.findById(campaignId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Campaign not found with id: " + campaignId));

            if (!campaign.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("Campaign does not belong to your workspace.");
            }

            DefinitionStatus newStatus = campaign.getStatus() == DefinitionStatus.ACTIVE
                    ? DefinitionStatus.INACTIVE
                    : DefinitionStatus.ACTIVE;
            campaign.setStatus(newStatus);

            Campaign saved = campaignRepository.save(campaign);

            String message = newStatus == DefinitionStatus.ACTIVE
                    ? "Campaign activated successfully."
                    : "Campaign deactivated successfully.";

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, message, toResponseDto(saved));
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    // delete campaign — MANAGER only
    public ResponseEntity<ApiResponseHandler<Object>> deleteCampaign(Long campaignId) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = user.getWorkspace();

            if (!user.getRole().equals(Role.MANAGER)) {
                throw new BadRequestException("Only a Manager can delete a campaign.");
            }

            Campaign campaign = campaignRepository.findById(campaignId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Campaign not found with id: " + campaignId));

            if (!campaign.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("Campaign does not belong to your workspace.");
            }

            campaignRepository.deleteById(campaignId);

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "Campaign deleted successfully.", null);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }
}
