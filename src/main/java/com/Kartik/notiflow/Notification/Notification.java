package com.Kartik.notiflow.Notification;
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
import java.nio.channels.Channel;
import org.hibernate.annotations.CreationTimestamp;

import com.Kartik.notiflow.Client.Client;
import com.Kartik.notiflow.Enum.DefinitionStatus;
import com.Kartik.notiflow.MessageTemplate.MessageTemplate;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long notificationId;
    @ManyToOne
    @JoinColumn(name = "clientId")
    private Client client; 
    @ManyToOne
    @JoinColumn(name = "messageTemplateId")
    private MessageTemplate messageTemplateId;
    @Enumerated(EnumType.STRING)
    private Channel channel;
    private String name;
    private String provider;
    @Enumerated(EnumType.STRING)
    private DefinitionStatus status;
    @CreationTimestamp
    private String createdAt;
}