package com.Kartik.notiflow.Entity;
import java.nio.channels.Channel;
import org.hibernate.annotations.CreationTimestamp;
import com.Kartik.notiflow.Enum.DefinitionStatus;
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
    private MessageTemplate messageTemplateId;
    @Enumerated(EnumType.STRING)
    private Channel channel;
    @Enumerated(EnumType.STRING)
    private DefinitionStatus status;
    @CreationTimestamp
    private String created_at;
}