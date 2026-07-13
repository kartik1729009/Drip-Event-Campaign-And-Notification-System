package com.Kartik.notiflow.CampaignInstance;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Kartik.notiflow.CampaignInstance.Dto.CreateCampaignInstanceDto;
import com.Kartik.notiflow.Common.Response.ApiResponseHandler;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/campaignInstance")
public class CampaignInstanceController {

    private final CampaignInstanceService campaignInstanceService;

    // user auth — create a campaign instance
    @PostMapping("/create")
    public ResponseEntity<ApiResponseHandler<Object>> createCampaignInstance(
            @RequestBody CreateCampaignInstanceDto request) {
        return campaignInstanceService.createCampaignInstance(request);
    }

    // user auth — MANAGER only: activate a campaign instance
    @PatchMapping("/activate/{instanceId}")
    public ResponseEntity<ApiResponseHandler<Object>> activateInstance(
            @PathVariable Long instanceId) {
        return campaignInstanceService.activateInstance(instanceId);
    }

    // user auth — get all instances for a campaign
    @GetMapping("/all/{campaignId}")
    public ResponseEntity<ApiResponseHandler<Object>> getAllByCampaign(
            @PathVariable Long campaignId) {
        return campaignInstanceService.getAllByCampaign(campaignId);
    }

    // user auth — get instance by id
    @GetMapping("/{instanceId}")
    public ResponseEntity<ApiResponseHandler<Object>> getInstanceById(
            @PathVariable Long instanceId) {
        return campaignInstanceService.getInstanceById(instanceId);
    }

    // user auth — MANAGER only: delete a campaign instance
    @DeleteMapping("/delete/{instanceId}")
    public ResponseEntity<ApiResponseHandler<Object>> deleteInstance(
            @PathVariable Long instanceId) {
        return campaignInstanceService.deleteInstance(instanceId);
    }
}
