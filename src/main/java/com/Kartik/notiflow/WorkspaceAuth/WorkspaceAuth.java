package com.Kartik.notiflow.WorkspaceAuth;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "workspaceAuth")

public class WorkspaceAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workspaceId;

    @Column(unique = true, nullable = false)
    private String workspaceName;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    private String callbackUrl;

    private String Api;

    private Boolean active = true;

    @CreationTimestamp
    private LocalDateTime createdAt;
}