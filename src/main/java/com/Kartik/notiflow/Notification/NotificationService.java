package com.Kartik.notiflow.Notification;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.Kartik.notiflow.Common.Exception.BadRequestException;
import com.Kartik.notiflow.Common.Exception.ResourceNotFoundException;
import com.Kartik.notiflow.Common.Response.ApiResponseHandler;
import com.Kartik.notiflow.Common.Response.ResponseBuilder;
import com.Kartik.notiflow.Enum.DefinitionStatus;
import com.Kartik.notiflow.Enum.Role;
import com.Kartik.notiflow.MessageTemplate.MessageTemplate;
import com.Kartik.notiflow.MessageTemplate.MessageTemplateRepository;
import com.Kartik.notiflow.Notification.Dto.CreateNotificationRequest;
import com.Kartik.notiflow.Notification.Dto.NotificationResponse;
import com.Kartik.notiflow.Notification.Dto.TriggerNotificationRequest;
import com.Kartik.notiflow.Security.CurrentAuth;
import com.Kartik.notiflow.UserAuth.UserAuth;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuth;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MessageTemplateRepository messageTemplateRepository;
    private final ModelMapper modelMapper;
    private final CurrentAuth currentAuth;

    private NotificationResponse toResponseDto(Notification notification) {
        NotificationResponse dto = modelMapper.map(notification, NotificationResponse.class);
        dto.setWorkspaceName(notification.getWorkspace().getWorkspaceName());
        dto.setCreatedByUserId(notification.getCreatedBy() != null ? notification.getCreatedBy().getUserAuthId() : null);
        dto.setMessageTemplateId(notification.getMessageTemplate().getMessageTemplateId());
        dto.setMessageTemplateName(notification.getMessageTemplate().getName());
        return dto;
    }

    public ResponseEntity<ApiResponseHandler<Object>> createNotification(CreateNotificationRequest request) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = user.getWorkspace();

            if (notificationRepository.existsByWorkspaceAndEventType(workspace, request.getEventType())) {
                throw new BadRequestException(
                        "A notification with eventType '" + request.getEventType() + "' already exists in this workspace.");
            }

            MessageTemplate messageTemplate = messageTemplateRepository
                    .findById(request.getMessageTemplateId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Message template not found with id: " + request.getMessageTemplateId()));

            if (!messageTemplate.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("Message template does not belong to your workspace.");
            }

            Notification notification = new Notification();
            notification.setWorkspace(workspace);
            notification.setCreatedBy(user);
            notification.setName(request.getName());
            notification.setEventType(request.getEventType());
            notification.setChannel(request.getChannel());
            notification.setMessageTemplate(messageTemplate);
            notification.setStatus(DefinitionStatus.ACTIVE);

            Notification saved = notificationRepository.save(notification);

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.CREATED, "Notification created successfully.", toResponseDto(saved));
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

    public ResponseEntity<ApiResponseHandler<Object>> getNotification() {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = user.getWorkspace();

            List<Notification> notifications = notificationRepository.findByWorkspace(workspace);
            List<NotificationResponse> dtos = notifications.stream().map(this::toResponseDto).toList();

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "Notifications retrieved successfully.", dtos);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    public ResponseEntity<ApiResponseHandler<Object>> getNotificationById(Long id) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = user.getWorkspace();

            Notification notification = notificationRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Notification not found with id: " + id));

            if (!notification.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("Notification does not belong to your workspace.");
            }

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "Notification retrieved successfully.", toResponseDto(notification));
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    public ResponseEntity<ApiResponseHandler<Object>> enableNotification(Long id) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = user.getWorkspace();

            if (!user.getRole().equals(Role.MANAGER)) {
                throw new BadRequestException("Only a Manager can enable a notification.");
            }

            Notification notification = notificationRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Notification not found with id: " + id));

            if (!notification.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("Notification does not belong to your workspace.");
            }

            notification.setStatus(DefinitionStatus.ACTIVE);
            Notification saved = notificationRepository.save(notification);

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "Notification enabled successfully.", toResponseDto(saved));
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    public ResponseEntity<ApiResponseHandler<Object>> disableNotification(Long id) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = user.getWorkspace();

            if (!user.getRole().equals(Role.MANAGER)) {
                throw new BadRequestException("Only a Manager can disable a notification.");
            }

            Notification notification = notificationRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Notification not found with id: " + id));

            if (!notification.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("Notification does not belong to your workspace.");
            }

            notification.setStatus(DefinitionStatus.INACTIVE);
            Notification saved = notificationRepository.save(notification);

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "Notification disabled successfully.", toResponseDto(saved));
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    public ResponseEntity<ApiResponseHandler<Object>> deleteNotification(Long id) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = user.getWorkspace();

            if (!user.getRole().equals(Role.MANAGER)) {
                throw new BadRequestException("Only a Manager can delete a notification.");
            }

            Notification notification = notificationRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Notification not found with id: " + id));

            if (!notification.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("Notification does not belong to your workspace.");
            }

            notificationRepository.deleteById(id);

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "Notification deleted successfully.", null);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    public ResponseEntity<ApiResponseHandler<Object>> triggerNotification(TriggerNotificationRequest request) {
        try {
            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "Notification triggered successfully.", null);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }
}
