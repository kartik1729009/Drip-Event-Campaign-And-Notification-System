package com.Kartik.notiflow.Campaign;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class CampaignDto {
    private Long CampaignId;
    private Long clientId;
    private String name;
    private String description;
    private LocalDate startDate;
    private String status;
    private LocalDateTime createdAt;
}