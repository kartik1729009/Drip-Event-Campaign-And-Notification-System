package com.Kartik.notiflow.DeliveryRecords;

import com.Kartik.notiflow.Campaign.Campaign;
import com.Kartik.notiflow.CampaignInstance.CampaignInstance;
import com.Kartik.notiflow.Client.Client;
import com.Kartik.notiflow.MessageInstance.MessageInstance;
import com.Kartik.notiflow.Notification.Notification;

import jakarta.persistence.Entity;
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
    @ManyToOne
    @JoinColumn(name = "clientId")
    private Client client;
    @ManyToOne
    @JoinColumn(name = "campaignId")
    private Campaign campaign;
    @ManyToOne
    @JoinColumn(name = "campaignInstanceId")
    private CampaignInstance campaignInstance;
    @ManyToOne
    @JoinColumn(name = "messageInstanceId")
    private MessageInstance messageInstance;
    @ManyToOne
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
