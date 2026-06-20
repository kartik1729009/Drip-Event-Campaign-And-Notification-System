package com.Kartik.notiflow.CampaignInstance;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.Kartik.notiflow.Campaign.Campaign;
import com.Kartik.notiflow.Enum.CampaignInstanceStatus;
import com.Kartik.notiflow.Enum.TriggerType;
import com.Kartik.notiflow.MessageInstance.MessageInstance;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "campaignInstance")

public class CampaignInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long campaignInstanceId;
    @ManyToOne
    @JoinColumn(name = "campaignId")
    private Campaign campaign;
    private int sequenceOrder;
    private int offsetMin;
    private String campaignInstanceName;
    private String absoluteTime;
    @Enumerated(EnumType.STRING)
    private TriggerType triggerType;
    private LocalDateTime nextExecutionTIme;
    @Enumerated(EnumType.STRING)
    private CampaignInstanceStatus status;
    @CreationTimestamp
    private String createdAt;
    @OneToMany(mappedBy = "campaignInstance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageInstance> messageInstances;
}
