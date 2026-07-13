package com.Kartik.notiflow.MessageInstance;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import com.Kartik.notiflow.Campaign.Campaign;
import com.Kartik.notiflow.CampaignInstance.CampaignInstance;
import com.Kartik.notiflow.Enum.Channel;
import com.Kartik.notiflow.MessageTemplate.MessageTemplate;
import com.Kartik.notiflow.UserAuth.UserAuth;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuth;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
    private Long messageInstanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspaceId", nullable = false)
    private WorkspaceAuth workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy")
    private UserAuth createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaignId", nullable = false)
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaignInstanceId", nullable = false)
    private CampaignInstance campaignInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "messageTemplateId", nullable = false)
    private MessageTemplate messageTemplate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Channel channel;

    @Column(nullable = false)
    private Integer sequenceOrder;

    @Column(nullable = false)
    private Boolean active = false;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
