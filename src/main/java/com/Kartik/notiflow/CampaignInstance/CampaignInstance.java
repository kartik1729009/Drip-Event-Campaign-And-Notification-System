package com.Kartik.notiflow.CampaignInstance;

import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import com.Kartik.notiflow.Campaign.Campaign;
import com.Kartik.notiflow.Enum.CampaignInstanceStatus;
import com.Kartik.notiflow.Enum.TriggerType;
import com.Kartik.notiflow.MessageInstance.MessageInstance;
import com.Kartik.notiflow.UserAuth.UserAuth;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuth;
import jakarta.persistence.CascadeType;
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
    private Long campaignInstanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaignId", nullable = false)
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspaceId", nullable = false)
    private WorkspaceAuth workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy")
    private UserAuth createdBy;

    private String campaignInstanceName;

    // sequence order provided by user — sequence 1 must always be ABSOLUTE
    @Column(nullable = false)
    private Integer sequenceOrder;

    // trigger type: ABSOLUTE or RELATIVE
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TriggerType triggerType;

    // offset in minutes — only for RELATIVE instances, computed from offsetHours + offsetMinutes in request
    private Integer offsetMin;

    // human-readable absolute time string e.g. "5:00 PM" — stored for reference
    private String absoluteTime;

    // computed execution datetime — for ABSOLUTE parsed from absoluteTime, for RELATIVE = prev nextExecutionTime + offsetMin
    @Column(nullable = false)
    private LocalDateTime nextExecutionTime;

    // default PENDING, reflects lifecycle of the instance
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CampaignInstanceStatus status = CampaignInstanceStatus.PENDING;

    // default false — only MANAGER can activate
    @Column(nullable = false)
    private Boolean active = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "campaignInstance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageInstance> messageInstances;
}
