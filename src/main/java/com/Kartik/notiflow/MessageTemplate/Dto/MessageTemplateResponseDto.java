package com.Kartik.notiflow.MessageTemplate.Dto;

import java.time.LocalDateTime;
import com.Kartik.notiflow.Enum.DefinitionStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageTemplateResponseDto {
    private Long messageTemplateId;
    private String workspaceName;
    private Long createdByUserId;
    private String name;
    private String description;
    private String content;
    private String version;  // formatted as "v1", "v2", etc.
    private DefinitionStatus status;
    private LocalDateTime createdAt;
}
