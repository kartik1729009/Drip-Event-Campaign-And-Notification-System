package com.Kartik.notiflow.Campaign.Dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CampaignRequestDto {
    private String name;
    private String description;
    private LocalDate startDate;
}
