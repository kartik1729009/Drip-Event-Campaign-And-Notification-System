package com.Kartik.notiflow.Campaign.Dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.Kartik.notiflow.Enum.DefinitionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CampaignResponseDto {
    private Long campaignId;
    private String workspaceName;
    private Long createdByUserId;
    private String name;
    private String description;
    private LocalDate startDate;
    private DefinitionStatus status;
    private LocalDateTime createdAt;
}
