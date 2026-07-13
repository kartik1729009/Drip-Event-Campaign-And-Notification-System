package com.Kartik.notiflow.CampaignInstance.Dto;

import com.Kartik.notiflow.Enum.TriggerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCampaignInstanceDto {

    private Long campaignId;

    private String campaignInstanceName;

    // sequence order — sequence 1 must always be ABSOLUTE
    private Integer sequenceOrder;

    // ABSOLUTE or RELATIVE
    private TriggerType triggerType;

    // for ABSOLUTE instances: datetime string in ISO format e.g. "2025-10-20T15:00:00"
    private String absoluteTime;

    // for RELATIVE instances: offset from previous instance's nextExecutionTime
    private Integer offsetHours;
    private Integer offsetMinutes;
}
