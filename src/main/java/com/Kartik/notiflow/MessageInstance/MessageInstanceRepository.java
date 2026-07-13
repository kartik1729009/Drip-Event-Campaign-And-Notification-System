package com.Kartik.notiflow.MessageInstance;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.Kartik.notiflow.CampaignInstance.CampaignInstance;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuth;

public interface MessageInstanceRepository extends JpaRepository<MessageInstance, Long> {

    List<MessageInstance> findByWorkspaceOrderBySequenceOrderAsc(WorkspaceAuth workspace);

    List<MessageInstance> findByCampaignInstanceOrderBySequenceOrderAsc(CampaignInstance campaignInstance);

    boolean existsByCampaignInstanceAndSequenceOrder(CampaignInstance campaignInstance, Integer sequenceOrder);
}
