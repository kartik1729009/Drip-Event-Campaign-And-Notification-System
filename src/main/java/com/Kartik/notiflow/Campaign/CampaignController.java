package com.Kartik.notiflow.Campaign;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Kartik.notiflow.Common.Response.ApiResponseHandler;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/campaign")
public class CampaignController {
    private final CampaignService campaignService;

    @RequestMapping("/createCampaign")
    public ResponseEntity<ApiResponseHandler<Object>> createCampaign(@RequestBody CampaignDto campaign){
        return campaignService.createCampaign(campaign);
    }
    @RequestMapping("/getAllCampaign")
    public ResponseEntity<ApiResponseHandler<Object>> getAllCampaign(){
        return campaignService.getAllCampaign();
    }
    @RequestMapping("/getCampaignById/{campaignId}")
    public ResponseEntity<ApiResponseHandler<Object>> getCampaignById(@PathVariable Long campaignId){
        return campaignService.getCampaignById(campaignId);
    }
}
