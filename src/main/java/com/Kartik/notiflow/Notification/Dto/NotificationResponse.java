package com.Kartik.notiflow.Notification.Dto;

import java.time.LocalDateTime;
import com.Kartik.notiflow.Enum.Channel;
import com.Kartik.notiflow.Enum.DefinitionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long notificationId;
    private String workspaceName;
    private Long createdByUserId;
    private String name;
    private String eventType;
    private Channel channel;
    private DefinitionStatus status;
    private Long messageTemplateId;
    private String messageTemplateName;
    private LocalDateTime createdAt;
}
