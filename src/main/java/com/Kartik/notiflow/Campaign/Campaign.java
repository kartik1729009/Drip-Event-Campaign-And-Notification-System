package com.Kartik.notiflow.Campaign;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.Kartik.notiflow.CampaignInstance.CampaignInstance;
import com.Kartik.notiflow.Client.Client;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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
    @ManyToOne
    @JoinColumn(name = "clientId")
    private Client client;
    @NotBlank(message = "Name date can't be blank")
    private String name;
    @NotBlank(message = "Description date can't be blank")
    private String description;
    @CreationTimestamp
    @NotBlank(message = "Start date can't be blank")
    private String startDate;
    private String status;
    @CreationTimestamp
    private String createdAt;
    @OneToMany(mappedBy = "Campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampaignInstance> campaignInstances;
}