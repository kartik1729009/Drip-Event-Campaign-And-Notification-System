package com.Kartik.notiflow.Campaign;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuth;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByWorkspace(WorkspaceAuth workspace);
}
