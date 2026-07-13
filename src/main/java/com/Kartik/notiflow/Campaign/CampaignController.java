package com.Kartik.notiflow.Campaign;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Kartik.notiflow.Campaign.Dto.CampaignRequestDto;
import com.Kartik.notiflow.Common.Response.ApiResponseHandler;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/campaign")
public class CampaignController {

    private final CampaignService campaignService;

    // user auth — create a campaign
    @PostMapping("/create")
    public ResponseEntity<ApiResponseHandler<Object>> createCampaign(
            @RequestBody CampaignRequestDto request) {
        return campaignService.createCampaign(request);
    }

    // user auth — get all campaigns in workspace
    @GetMapping("/all")
    public ResponseEntity<ApiResponseHandler<Object>> getAllCampaign() {
        return campaignService.getAllCampaign();
    }

    // user auth — get campaign by id
    @GetMapping("/{campaignId}")
    public ResponseEntity<ApiResponseHandler<Object>> getCampaignById(
            @PathVariable Long campaignId) {
        return campaignService.getCampaignById(campaignId);
    }

    // user auth — update campaign name, description, startDate
    @PatchMapping("/update/{campaignId}")
    public ResponseEntity<ApiResponseHandler<Object>> patchCampaign(
            @PathVariable Long campaignId,
            @RequestBody CampaignRequestDto request) {
        return campaignService.patchCampaign(campaignId, request);
    }

    // user auth — MANAGER only: toggle campaign status ACTIVE <-> INACTIVE
    @PatchMapping("/toggleStatus/{campaignId}")
    public ResponseEntity<ApiResponseHandler<Object>> toggleStatus(
            @PathVariable Long campaignId) {
        return campaignService.toggleStatus(campaignId);
    }

    // user auth — MANAGER only: delete a campaign
    @DeleteMapping("/delete/{campaignId}")
    public ResponseEntity<ApiResponseHandler<Object>> deleteCampaign(
            @PathVariable Long campaignId) {
        return campaignService.deleteCampaign(campaignId);
    }
}
