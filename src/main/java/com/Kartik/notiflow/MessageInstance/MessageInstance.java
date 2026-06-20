package com.Kartik.notiflow.MessageInstance;

import com.Kartik.notiflow.Enum.Channel;
import org.hibernate.annotations.CreationTimestamp;
import com.Kartik.notiflow.CampaignInstance.CampaignInstance;
import com.Kartik.notiflow.Enum.DefinitionStatus;
import com.Kartik.notiflow.MessageTemplate.MessageTemplate;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "messageInstance")

public class MessageInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long messageInstanceId;
    @ManyToOne
    @JoinColumn(name = "campaignInstanceId")
    private CampaignInstance campaignInstance;
    @ManyToOne
    @JoinColumn(name = "messageTemplateId")
    private MessageTemplate messageTemplate;
    @Enumerated(EnumType.STRING)
    private Channel channel;
    @Enumerated(EnumType.STRING)
    private DefinitionStatus status;
    @CreationTimestamp
    private String created_at;
}