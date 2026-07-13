package com.Kartik.notiflow.MessageInstance.Dto;

import java.time.LocalDateTime;
import com.Kartik.notiflow.Enum.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageInstanceResponseDto {
    private Long messageInstanceId;
    private String workspaceName;
    private Long createdByUserId;
    private Long campaignId;
    private String campaignName;
    private Long campaignInstanceId;
    private String campaignInstanceName;
    private Long messageTemplateId;
    private String messageTemplateName;
    private String messageTemplateContent;
    private String messageTemplateVersion;
    private Channel channel;
    private Integer sequenceOrder;
    private Boolean active;
    private LocalDateTime createdAt;
}
