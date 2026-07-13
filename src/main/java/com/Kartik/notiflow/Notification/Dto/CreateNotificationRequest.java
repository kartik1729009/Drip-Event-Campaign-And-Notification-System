package com.Kartik.notiflow.Notification.Dto;

import com.Kartik.notiflow.Enum.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {
    private String name;
    private String eventType;
    private Long messageTemplateId;
    private Channel channel;
}
