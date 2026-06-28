package com.Kartik.notiflow.Notification;

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
import org.hibernate.annotations.CreationTimestamp;
import com.Kartik.notiflow.Enum.Channel;
import com.Kartik.notiflow.Enum.DefinitionStatus;
import com.Kartik.notiflow.MessageTemplate.MessageTemplate;
import com.Kartik.notiflow.UserAuth.UserAuth;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuth;

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
    @JoinColumn(name = "workspaceId", nullable = false)
    private WorkspaceAuth workspace;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy")
    private UserAuth createdBy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "messageTemplateId")
    private MessageTemplate messageTemplate;
    @Enumerated(EnumType.STRING)
    private Channel channel;
    private String name;
    private String provider;
    @Enumerated(EnumType.STRING)
    private DefinitionStatus status;
    @CreationTimestamp
    private String createdAt;
}