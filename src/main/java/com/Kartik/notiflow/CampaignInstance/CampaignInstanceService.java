package com.Kartik.notiflow.CampaignInstance;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.Kartik.notiflow.CampaignInstance.Dto.CampaignInstanceResponseDto;
import com.Kartik.notiflow.CampaignInstance.Dto.CreateCampaignInstanceDto;
import com.Kartik.notiflow.Campaign.Campaign;
import com.Kartik.notiflow.Campaign.CampaignRepository;
import com.Kartik.notiflow.Common.Exception.BadRequestException;
import com.Kartik.notiflow.Common.Exception.ResourceNotFoundException;
import com.Kartik.notiflow.Common.Response.ApiResponseHandler;
import com.Kartik.notiflow.Common.Response.ResponseBuilder;
import com.Kartik.notiflow.Enum.CampaignInstanceStatus;
import com.Kartik.notiflow.Enum.Role;
import com.Kartik.notiflow.Enum.TriggerType;
import com.Kartik.notiflow.Security.CurrentAuth;
import com.Kartik.notiflow.UserAuth.UserAuth;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuth;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampaignInstanceService {

    private final CampaignInstanceRepository campaignInstanceRepository;
    private final CampaignRepository campaignRepository;
    private final ModelMapper modelMapper;
    private final CurrentAuth currentAuth;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    // helper: map entity -> response dto
    private CampaignInstanceResponseDto toResponseDto(CampaignInstance instance) {
        CampaignInstanceResponseDto dto = modelMapper.map(instance, CampaignInstanceResponseDto.class);
        dto.setCampaignId(instance.getCampaign().getCampaignId());
        dto.setCampaignName(instance.getCampaign().getName());
        dto.setWorkspaceName(instance.getWorkspace().getWorkspaceName());
        dto.setCreatedByUserId(instance.getCreatedBy() != null ? instance.getCreatedBy().getUserAuthId() : null);
        return dto;
    }

    // create campaign instance
    public ResponseEntity<ApiResponseHandler<Object>> createCampaignInstance(CreateCampaignInstanceDto request) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = user.getWorkspace();

            Campaign campaign = campaignRepository.findById(request.getCampaignId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Campaign not found with id: " + request.getCampaignId()));

            if (!campaign.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("Campaign does not belong to your workspace.");
            }

            // sequence 1 must always be ABSOLUTE
            if (request.getSequenceOrder() == 1 && request.getTriggerType() != TriggerType.ABSOLUTE) {
                throw new BadRequestException("First instance (sequence 1) must always be ABSOLUTE.");
            }

            // prevent duplicate sequence order for same campaign
            if (campaignInstanceRepository.existsByCampaignAndSequenceOrder(campaign, request.getSequenceOrder())) {
                throw new BadRequestException(
                        "Sequence order " + request.getSequenceOrder() + " already exists for this campaign.");
            }

            LocalDateTime nextExecutionTime;
            Integer offsetMin = null;
            String absoluteTimeStr = null;

            if (request.getTriggerType() == TriggerType.ABSOLUTE) {
                // parse absoluteTime from request body
                if (request.getAbsoluteTime() == null || request.getAbsoluteTime().isBlank()) {
                    throw new BadRequestException("absoluteTime is required for ABSOLUTE trigger type.");
                }
                try {
                    nextExecutionTime = LocalDateTime.parse(request.getAbsoluteTime(), FORMATTER);
                } catch (DateTimeParseException ex) {
                    throw new BadRequestException(
                            "Invalid absoluteTime format. Expected: yyyy-MM-ddTHH:mm:ss e.g. 2025-10-20T15:00:00");
                }
                absoluteTimeStr = request.getAbsoluteTime();

            } else {
                // RELATIVE — compute offsetMin from offsetHours + offsetMinutes
                if (request.getOffsetHours() == null && request.getOffsetMinutes() == null) {
                    throw new BadRequestException("offsetHours and/or offsetMinutes are required for RELATIVE trigger type.");
                }
                int hours = request.getOffsetHours() != null ? request.getOffsetHours() : 0;
                int minutes = request.getOffsetMinutes() != null ? request.getOffsetMinutes() : 0;
                offsetMin = (hours * 60) + minutes;

                if (offsetMin <= 0) {
                    throw new BadRequestException("Total offset must be greater than 0 minutes.");
                }

                // get previous instance to compute next execution time
                CampaignInstance prevInstance = campaignInstanceRepository
                        .findByCampaignAndSequenceOrder(campaign, request.getSequenceOrder() - 1)
                        .orElseThrow(() -> new BadRequestException(
                                "Previous instance (sequence " + (request.getSequenceOrder() - 1)
                                        + ") not found. Instances must be created in sequence order."));

                nextExecutionTime = prevInstance.getNextExecutionTime().plusMinutes(offsetMin);

                // format computed time as human-readable absolute time string e.g. "2025-10-20T17:00:00"
                absoluteTimeStr = nextExecutionTime.format(FORMATTER);
            }

            // nextExecutionTime must be >= campaign startDate
            LocalDateTime campaignStart = campaign.getStartDate().atStartOfDay();
            if (nextExecutionTime.isBefore(campaignStart)) {
                throw new BadRequestException(
                        "Instance execution time must be greater than or equal to campaign start date: "
                                + campaign.getStartDate());
            }

            CampaignInstance instance = new CampaignInstance();
            instance.setCampaign(campaign);
            instance.setWorkspace(workspace);
            instance.setCreatedBy(user);
            instance.setCampaignInstanceName(request.getCampaignInstanceName());
            instance.setSequenceOrder(request.getSequenceOrder());
            instance.setTriggerType(request.getTriggerType());
            instance.setOffsetMin(offsetMin);
            instance.setAbsoluteTime(absoluteTimeStr);
            instance.setNextExecutionTime(nextExecutionTime);
            instance.setStatus(CampaignInstanceStatus.PENDING);
            instance.setActive(false);

            CampaignInstance saved = campaignInstanceRepository.save(instance);

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.CREATED, "Campaign instance created successfully.", toResponseDto(saved));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Database error: invalid data constraint.");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    // activate instance — MANAGER only
    public ResponseEntity<ApiResponseHandler<Object>> activateInstance(Long instanceId) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = user.getWorkspace();

            if (!user.getRole().equals(Role.MANAGER)) {
                throw new BadRequestException("Only a Manager can activate a campaign instance.");
            }

            CampaignInstance instance = campaignInstanceRepository.findById(instanceId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Campaign instance not found with id: " + instanceId));

            if (!instance.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("Campaign instance does not belong to your workspace.");
            }

            instance.setActive(true);
            instance.setStatus(CampaignInstanceStatus.SCHEDULED);
            CampaignInstance saved = campaignInstanceRepository.save(instance);

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "Campaign instance activated successfully.", toResponseDto(saved));
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    // get all instances for a campaign
    public ResponseEntity<ApiResponseHandler<Object>> getAllByCampaign(Long campaignId) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = user.getWorkspace();

            Campaign campaign = campaignRepository.findById(campaignId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Campaign not found with id: " + campaignId));

            if (!campaign.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("Campaign does not belong to your workspace.");
            }

            List<CampaignInstance> instances = campaignInstanceRepository
                    .findByCampaignOrderBySequenceOrderAsc(campaign);
            List<CampaignInstanceResponseDto> dtos = instances.stream().map(this::toResponseDto).toList();

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "Campaign instances retrieved successfully.", dtos);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    // get instance by id
    public ResponseEntity<ApiResponseHandler<Object>> getInstanceById(Long instanceId) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = user.getWorkspace();

            CampaignInstance instance = campaignInstanceRepository.findById(instanceId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Campaign instance not found with id: " + instanceId));

            if (!instance.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("Campaign instance does not belong to your workspace.");
            }

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "Campaign instance retrieved successfully.", toResponseDto(instance));
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    // delete instance — MANAGER only
    public ResponseEntity<ApiResponseHandler<Object>> deleteInstance(Long instanceId) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = user.getWorkspace();

            if (!user.getRole().equals(Role.MANAGER)) {
                throw new BadRequestException("Only a Manager can delete a campaign instance.");
            }

            CampaignInstance instance = campaignInstanceRepository.findById(instanceId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Campaign instance not found with id: " + instanceId));

            if (!instance.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("Campaign instance does not belong to your workspace.");
            }

            campaignInstanceRepository.deleteById(instanceId);

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "Campaign instance deleted successfully.", null);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }
}
