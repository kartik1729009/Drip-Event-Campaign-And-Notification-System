package com.Kartik.notiflow.MessageInstance;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Kartik.notiflow.Common.Response.ApiResponseHandler;
import com.Kartik.notiflow.MessageInstance.Dto.CreateMessageInstance;
import com.Kartik.notiflow.MessageInstance.Dto.UpdateMessageInstance;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messageInstance")
public class MessageInstanceController {

    private final MessageInstanceService messageInstanceService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponseHandler<Object>> create(
            @RequestBody CreateMessageInstance request) {
        return messageInstanceService.create(request);
    }

    @PatchMapping("/activate/{messageInstanceId}")
    public ResponseEntity<ApiResponseHandler<Object>> activate(
            @PathVariable Long messageInstanceId) {
        return messageInstanceService.activate(messageInstanceId);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponseHandler<Object>> getAll() {
        return messageInstanceService.getAll();
    }

    @GetMapping("/byCampaignInstance/{campaignInstanceId}")
    public ResponseEntity<ApiResponseHandler<Object>> getByCampaignInstance(
            @PathVariable Long campaignInstanceId) {
        return messageInstanceService.getByCampaignInstance(campaignInstanceId);
    }

    @PatchMapping("/update/{messageInstanceId}")
    public ResponseEntity<ApiResponseHandler<Object>> update(
            @PathVariable Long messageInstanceId,
            @RequestBody UpdateMessageInstance request) {
        return messageInstanceService.update(messageInstanceId, request);
    }

    @DeleteMapping("/delete/{messageInstanceId}")
    public ResponseEntity<ApiResponseHandler<Object>> delete(
            @PathVariable Long messageInstanceId) {
        return messageInstanceService.delete(messageInstanceId);
    }
}
