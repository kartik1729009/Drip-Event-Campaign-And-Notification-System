package com.Kartik.notiflow.CampaignInstance;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.Kartik.notiflow.Campaign.Campaign;

public interface CampaignInstanceRepository extends JpaRepository<CampaignInstance, Long> {

    // get all instances for a campaign ordered by sequence
    List<CampaignInstance> findByCampaignOrderBySequenceOrderAsc(Campaign campaign);

    // check if a sequence order already exists for a campaign — prevent duplicates
    boolean existsByCampaignAndSequenceOrder(Campaign campaign, Integer sequenceOrder);

    // get previous instance by sequence order — used to compute relative nextExecutionTime
    Optional<CampaignInstance> findByCampaignAndSequenceOrder(Campaign campaign, Integer sequenceOrder);
}
