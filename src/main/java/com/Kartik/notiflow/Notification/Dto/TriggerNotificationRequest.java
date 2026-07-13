package com.Kartik.notiflow.Notification.Dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TriggerNotificationRequest {
    private String eventType;
    private Recipient recipient;
    private Map<String, Object> variables;
}
