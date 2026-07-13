package com.Kartik.notiflow.Campaign.Dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.Kartik.notiflow.UserAuth.UserAuth;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class CampaignDto {
    private WorkspaceAuth workspace;
    private UserAuth createdBy;
    private String name;
    private String description;
    private LocalDate startDate;
    private String status;
    private LocalDateTime createdAt;
}