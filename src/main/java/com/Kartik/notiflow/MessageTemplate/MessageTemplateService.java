package com.Kartik.notiflow.MessageTemplate;

import java.util.List;
import java.util.Optional;
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
import com.Kartik.notiflow.MessageTemplate.Dto.MessageTemplateRequestDto;
import com.Kartik.notiflow.MessageTemplate.Dto.MessageTemplateResponseDto;
import com.Kartik.notiflow.Security.CurrentAuth;
import com.Kartik.notiflow.UserAuth.UserAuth;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuth;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageTemplateService {

    private final MessageTemplateRepository messageTemplateRepository;
    private final ModelMapper modelMapper;
    private final CurrentAuth currentAuth;

    // helper: map entity -> response dto
    private MessageTemplateResponseDto toResponseDto(MessageTemplate template) {
        MessageTemplateResponseDto dto = modelMapper.map(template, MessageTemplateResponseDto.class);
        dto.setVersion("v" + template.getVersion());
        dto.setWorkspaceName(template.getWorkspace().getWorkspaceName());
        dto.setCreatedByUserId(template.getCreatedBy() != null ? template.getCreatedBy().getUserAuthId() : null);
        return dto;
    }

    // create a new template or a new version if same name already exists in workspace
    public ResponseEntity<ApiResponseHandler<Object>> createTemplate(MessageTemplateRequestDto request) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = currentAuth.getWorkspace();

            // find latest version for this name+workspace to determine next version number
            Optional<MessageTemplate> latest = messageTemplateRepository
                    .findTopByNameAndWorkspaceOrderByVersionDesc(request.getName(), workspace);

            int nextVersion = latest.map(t -> t.getVersion() + 1).orElse(1);

            MessageTemplate template = new MessageTemplate();
            template.setWorkspace(workspace);
            template.setCreatedBy(user);
            template.setName(request.getName());
            template.setDescription(request.getDescription());
            template.setContent(request.getContent());
            template.setVersion(nextVersion);
            template.setStatus(DefinitionStatus.INACTIVE); // default inactive

            MessageTemplate saved = messageTemplateRepository.save(template);

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.CREATED,
                    "Message template created successfully as v" + nextVersion,
                    toResponseDto(saved));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Database error: invalid data constraint.");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    // activate a template — Manager only
    public ResponseEntity<ApiResponseHandler<Object>> activateTemplate(Long templateId) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = currentAuth.getWorkspace();

            if (!user.getRole().equals(Role.MANAGER)) {
                throw new BadRequestException("Only a Manager can activate a template.");
            }

            MessageTemplate template = messageTemplateRepository.findById(templateId)
                    .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + templateId));

            if (!template.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("Template does not belong to your workspace.");
            }

            template.setStatus(DefinitionStatus.ACTIVE);
            MessageTemplate saved = messageTemplateRepository.save(template);

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK,
                    "Template activated successfully.",
                    toResponseDto(saved));
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    // update name, description, content — Manager only
    public ResponseEntity<ApiResponseHandler<Object>> updateTemplate(Long templateId, MessageTemplateRequestDto request) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = currentAuth.getWorkspace();

            if (!user.getRole().equals(Role.MANAGER)) {
                throw new BadRequestException("Only a Manager can update a template.");
            }

            MessageTemplate template = messageTemplateRepository.findById(templateId)
                    .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + templateId));

            if (!template.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("Template does not belong to your workspace.");
            }

            Optional.ofNullable(request.getName()).ifPresent(template::setName);
            Optional.ofNullable(request.getDescription()).ifPresent(template::setDescription);
            Optional.ofNullable(request.getContent()).ifPresent(template::setContent);

            MessageTemplate saved = messageTemplateRepository.save(template);

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK,
                    "Template updated successfully.",
                    toResponseDto(saved));
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    // delete a template — Manager only
    public ResponseEntity<ApiResponseHandler<Object>> deleteTemplate(Long templateId) {
        try {
            UserAuth user = currentAuth.getUser();
            WorkspaceAuth workspace = currentAuth.getWorkspace();

            if (!user.getRole().equals(Role.MANAGER)) {
                throw new BadRequestException("Only a Manager can delete a template.");
            }

            MessageTemplate template = messageTemplateRepository.findById(templateId)
                    .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + templateId));

            if (!template.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("Template does not belong to your workspace.");
            }

            messageTemplateRepository.deleteById(templateId);

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "Template deleted successfully.", null);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    // get latest version of a template by name
    public ResponseEntity<ApiResponseHandler<Object>> getLatestByName(String name) {
        try {
            WorkspaceAuth workspace = currentAuth.getWorkspace();

            MessageTemplate latest = messageTemplateRepository
                    .findTopByNameAndWorkspaceOrderByVersionDesc(name, workspace)
                    .orElseThrow(() -> new ResourceNotFoundException("No template found with name: " + name));

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "Latest template version retrieved.", toResponseDto(latest));
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    // get all versions of a template by name
    public ResponseEntity<ApiResponseHandler<Object>> getAllVersionsByName(String name) {
        try {
            WorkspaceAuth workspace = currentAuth.getWorkspace();

            List<MessageTemplate> versions = messageTemplateRepository
                    .findByNameAndWorkspace(name, workspace);

            if (versions.isEmpty()) {
                throw new ResourceNotFoundException("No template found with name: " + name);
            }

            List<MessageTemplateResponseDto> dtos = versions.stream().map(this::toResponseDto).toList();

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "All versions retrieved.", dtos);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    // get all templates in the workspace
    public ResponseEntity<ApiResponseHandler<Object>> getAllTemplates() {
        try {
            WorkspaceAuth workspace = currentAuth.getWorkspace();

            List<MessageTemplate> templates = messageTemplateRepository.findByWorkspace(workspace);
            List<MessageTemplateResponseDto> dtos = templates.stream().map(this::toResponseDto).toList();

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "All templates retrieved.", dtos);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }
}
