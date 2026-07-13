package com.Kartik.notiflow.MessageTemplate;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuth;

public interface MessageTemplateRepository extends JpaRepository<MessageTemplate, Long> {

    // get the highest version for a given name+workspace to auto-increment
    Optional<MessageTemplate> findTopByNameAndWorkspaceOrderByVersionDesc(String name, WorkspaceAuth workspace);

    // get all versions of a template by name within a workspace
    List<MessageTemplate> findByNameAndWorkspace(String name, WorkspaceAuth workspace);

    // get all templates in a workspace
    List<MessageTemplate> findByWorkspace(WorkspaceAuth workspace);
}
