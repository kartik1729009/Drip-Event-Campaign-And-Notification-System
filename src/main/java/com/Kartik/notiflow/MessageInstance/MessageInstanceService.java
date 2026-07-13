package com.Kartik.notiflow.MessageInstance;

import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.Kartik.notiflow.CampaignInstance.CampaignInstance;
import com.Kartik.notiflow.CampaignInstance.CampaignInstanceRepository;
import com.Kartik.notiflow.Common.Exception.BadRequestException;
import com.Kartik.notiflow.Common.Exception.ResourceNotFoundException;
import com.Kartik.notiflow.Common.Response.ApiResponseHandler;
import com.Kartik.notiflow.Common.Response.ResponseBuilder;
import com.Kartik.notiflow.Enum.Role;
import com.Kartik.notiflow.MessageInstance.Dto.CreateMessageInstance;
import com.Kartik.notiflow.MessageInstance.Dto.MessageInstanceResponseDto;
import com.Kartik.notiflow.MessageInstance.Dto.UpdateMessageInstance;
import com.Kartik.notiflow.MessageTemplate.MessageTemplate;
import com.Kartik.notiflow.MessageTemplate.MessageTemplateRepository;
import com.Kartik.notiflow.Security.CurrentAuth;
import com.Kartik.notiflow.UserAuth.UserAuth;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuth;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageInstanceService {

    private final MessageInstanceRepository messageInstanceRepository;
    private final CampaignInstanceRepository campaignInstanceRepository;
    private final MessageTemplateRepository messageTemplateRepository;
    private final ModelMapper modelMapper;
    private final CurrentAuth currentAuth;

    private MessageInstanceResponseDto toResponseDto(MessageInstance mi) {
        MessageInstanceResponseDto dto = modelMapper.map(mi, MessageInstanceResponseDto.class);
        dto.setWorkspaceName(mi.getWorkspace().getWorkspaceName());
        dto.setCreatedByUserId(mi.getCreatedBy() != null ? mi.getCreatedBy().getUserAuthId() : null);
        dto.setCampaignId(mi.getCampaign().getCampaignId());
        dto.setCampaignName(mi.getCampaign().getName());
        dto.setCampaignInstanceId(mi.getCampaignInstance().getCampaignInstanceId());
        dto.setCampaignInstanceName(mi.getCampaignInstance().getCampaignInstanceName());
        dto.setMessageTemplateId(mi.getMessageTemplate().getMessageTemplateId());
        dto.setMessageTemplateName(mi.getMessageTemplate().getName());
        dto.setMessageTemplateContent(mi.getMessageTemplate().getContent());
        dto.setMessageTemplateVersion("v" + mi.getMessageTemplate().getVersion());
        return dto;
    }

    public ResponseEntity<ApiResponseHandler<Object>> create(CreateMessageInstance request) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = user.getWorkspace();

            CampaignInstance campaignInstance = campaignInstanceRepository
                    .findById(request.getCampaignInstanceId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Campaign instance not found with id: " + request.getCampaignInstanceId()));

            if (!campaignInstance.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("Campaign instance does not belong to your workspace.");
            }

            MessageTemplate messageTemplate = messageTemplateRepository
                    .findById(request.getMessageTemplateId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Message template not found with id: " + request.getMessageTemplateId()));

            if (!messageTemplate.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("Message template does not belong to your workspace.");
            }

            if (request.getSequenceOrder() == null || request.getSequenceOrder() <= 0) {
                throw new BadRequestException("sequenceOrder is required and must be greater than 0.");
            }

            if (messageInstanceRepository.existsByCampaignInstanceAndSequenceOrder(
                    campaignInstance, request.getSequenceOrder())) {
                throw new BadRequestException(
                        "Sequence order " + request.getSequenceOrder() + " already exists for this campaign instance.");
            }

            MessageInstance mi = new MessageInstance();
            mi.setWorkspace(workspace);
            mi.setCreatedBy(user);
            mi.setCampaign(campaignInstance.getCampaign());
            mi.setCampaignInstance(campaignInstance);
            mi.setMessageTemplate(messageTemplate);
            mi.setChannel(request.getChannel());
            mi.setSequenceOrder(request.getSequenceOrder());
            mi.setActive(false);

            MessageInstance saved = messageInstanceRepository.save(mi);

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.CREATED, "Message instance created successfully.", toResponseDto(saved));
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

    public ResponseEntity<ApiResponseHandler<Object>> activate(Long messageInstanceId) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = user.getWorkspace();

            if (!user.getRole().equals(Role.MANAGER)) {
                throw new BadRequestException("Only a Manager can activate a message instance.");
            }

            MessageInstance mi = messageInstanceRepository.findById(messageInstanceId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Message instance not found with id: " + messageInstanceId));

            if (!mi.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("Message instance does not belong to your workspace.");
            }

            mi.setActive(true);
            MessageInstance saved = messageInstanceRepository.save(mi);

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "Message instance activated successfully.", toResponseDto(saved));
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    public ResponseEntity<ApiResponseHandler<Object>> getAll() {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = user.getWorkspace();

            List<MessageInstance> instances = messageInstanceRepository
                    .findByWorkspaceOrderBySequenceOrderAsc(workspace);
            List<MessageInstanceResponseDto> dtos = instances.stream().map(this::toResponseDto).toList();

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "Message instances retrieved successfully.", dtos);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    public ResponseEntity<ApiResponseHandler<Object>> getByCampaignInstance(Long campaignInstanceId) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = user.getWorkspace();

            CampaignInstance campaignInstance = campaignInstanceRepository
                    .findById(campaignInstanceId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Campaign instance not found with id: " + campaignInstanceId));

            if (!campaignInstance.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("Campaign instance does not belong to your workspace.");
            }

            List<MessageInstance> instances = messageInstanceRepository
                    .findByCampaignInstanceOrderBySequenceOrderAsc(campaignInstance);
            List<MessageInstanceResponseDto> dtos = instances.stream().map(this::toResponseDto).toList();

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "Message instances retrieved successfully.", dtos);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    public ResponseEntity<ApiResponseHandler<Object>> update(Long messageInstanceId, UpdateMessageInstance request) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = user.getWorkspace();

            MessageInstance mi = messageInstanceRepository.findById(messageInstanceId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Message instance not found with id: " + messageInstanceId));

            if (!mi.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("Message instance does not belong to your workspace.");
            }

            Optional.ofNullable(request.getChannel()).ifPresent(mi::setChannel);

            if (request.getMessageTemplateId() != null) {
                MessageTemplate messageTemplate = messageTemplateRepository
                        .findById(request.getMessageTemplateId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Message template not found with id: " + request.getMessageTemplateId()));
                if (!messageTemplate.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                    throw new BadRequestException("Message template does not belong to your workspace.");
                }
                mi.setMessageTemplate(messageTemplate);
            }

            MessageInstance saved = messageInstanceRepository.save(mi);

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "Message instance updated successfully.", toResponseDto(saved));
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    public ResponseEntity<ApiResponseHandler<Object>> delete(Long messageInstanceId) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = user.getWorkspace();

            if (!user.getRole().equals(Role.MANAGER)) {
                throw new BadRequestException("Only a Manager can delete a message instance.");
            }

            MessageInstance mi = messageInstanceRepository.findById(messageInstanceId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Message instance not found with id: " + messageInstanceId));

            if (!mi.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("Message instance does not belong to your workspace.");
            }

            messageInstanceRepository.deleteById(messageInstanceId);

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "Message instance deleted successfully.", null);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }
}
