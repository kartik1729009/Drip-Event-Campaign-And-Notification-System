package com.Kartik.notiflow.WorkspaceAuth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceAuthRepository extends JpaRepository<WorkspaceAuth, Long> {
    Optional<WorkspaceAuth> findByWorkspaceName(String workspaceName);

    Optional<WorkspaceAuth> findByUsername(String username);
}
