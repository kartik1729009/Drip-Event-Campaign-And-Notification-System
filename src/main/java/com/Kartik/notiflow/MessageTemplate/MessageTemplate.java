package com.Kartik.notiflow.MessageTemplate;

import org.hibernate.annotations.CreationTimestamp;
import com.Kartik.notiflow.UserAuth.UserAuth;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuth;

import jakarta.persistence.Entity;
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
@Table(name = "messageTemplate")

public class MessageTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long messageTemplateId;
    @JoinColumn(name = "workspaceId", nullable = false)
    private WorkspaceAuth workspace;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy")
    private UserAuth createdBy;
    private String name;
    private String content;
    private String version;
    private boolean active;
    @CreationTimestamp
    private String created_at;
}
