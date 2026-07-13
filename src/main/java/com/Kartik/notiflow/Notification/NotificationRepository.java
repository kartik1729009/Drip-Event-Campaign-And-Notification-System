package com.Kartik.notiflow.Notification;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.Kartik.notiflow.Enum.DefinitionStatus;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuth;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByWorkspace(WorkspaceAuth workspace);

    boolean existsByWorkspaceAndEventType(WorkspaceAuth workspace, String eventType);

    Optional<Notification> findByWorkspaceAndEventTypeAndStatus(
            WorkspaceAuth workspace, String eventType, DefinitionStatus status);
}
