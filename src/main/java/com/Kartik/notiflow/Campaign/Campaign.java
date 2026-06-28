package com.Kartik.notiflow.Campaign;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import com.Kartik.notiflow.CampaignInstance.CampaignInstance;
import com.Kartik.notiflow.UserAuth.UserAuth;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuth;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "campaign")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long campaignId;
    @JoinColumn(name = "workspaceId", nullable = false)
    private WorkspaceAuth workspace;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy")
    private UserAuth createdBy;
    @NotBlank(message = "Name date can't be blank")
    private String name;
    @NotBlank(message = "Description date can't be blank")
    private String description;
    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;
    private String status;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampaignInstance> campaignInstances;
}