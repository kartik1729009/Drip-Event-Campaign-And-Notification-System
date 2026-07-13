package com.Kartik.notiflow.MessageTemplate.Dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageTemplateRequestDto {
    private String name;
    private String description;
    private String content;
}
