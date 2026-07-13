package com.Kartik.notiflow.CampaignInstance.Dto;

import java.time.LocalDateTime;
import com.Kartik.notiflow.Enum.CampaignInstanceStatus;
import com.Kartik.notiflow.Enum.TriggerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CampaignInstanceResponseDto {

    private Long campaignInstanceId;
    private Long campaignId;
    private String campaignName;
    private String workspaceName;
    private Long createdByUserId;
    private String campaignInstanceName;
    private Integer sequenceOrder;
    private TriggerType triggerType;
    private Integer offsetMin;
    private String absoluteTime;
    private LocalDateTime nextExecutionTime;
    private CampaignInstanceStatus status;
    private Boolean active;
    private LocalDateTime createdAt;
}
