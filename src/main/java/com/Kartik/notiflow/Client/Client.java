package com.Kartik.notiflow.Client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "Client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientId;
    @NotBlank(message = "Name can't be blank")
    private String name;
    @NotBlank(message = "UserName can't be blank")
    @Column(unique = true)
    private String userName;
    @NotBlank(message = "Workspace can't be blank")
    @Size(min = 5, max = 30, message = "Workspace size must be between 5-30")
    @NotBlank(message = "Workspace can't be blank")
    @Column(unique = true, nullable = false)
    private String workspace;
    private String apiKey;
    private String status;
    private String callbackUrl;
    @CreationTimestamp
    private LocalDateTime createdAt;
}