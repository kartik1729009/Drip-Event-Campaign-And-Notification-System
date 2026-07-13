package com.Kartik.notiflow.MessageInstance.Dto;

import com.Kartik.notiflow.Enum.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMessageInstance {
    private Long messageTemplateId;
    private Channel channel;
}
