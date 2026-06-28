package com.Kartik.notiflow.DeliveryRecords;

import com.Kartik.notiflow.Campaign.Campaign;
import com.Kartik.notiflow.CampaignInstance.CampaignInstance;
import com.Kartik.notiflow.MessageInstance.MessageInstance;
import com.Kartik.notiflow.Notification.Notification;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.GenerationType;
import com.Kartik.notiflow.UserAuth.UserAuth;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuth;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "deliveryRecord")

public class DeliveryRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long deliveryRecordId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspaceId", nullable = false)
    private WorkspaceAuth workspace;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy")
    private UserAuth createdBy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaignId")
    private Campaign campaign;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaignInstanceId")
    private CampaignInstance campaignInstance;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "messageInstanceId")
    private MessageInstance messageInstance;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notificationId")
    private Notification notification;
    // private long notificationDefinitionId;
    private long providerMessageId;
    private int retryCount;
    private String recipient;
    private String channel;
    private String provider;
    private String status;
    private String failureReason;
    private String createdAt;
    private String updatedAt;

}
