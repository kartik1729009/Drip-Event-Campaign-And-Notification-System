package com.Kartik.notiflow.Notification;

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
import com.Kartik.notiflow.Notification.Dto.CreateNotificationRequest;
import com.Kartik.notiflow.Notification.Dto.TriggerNotificationRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponseHandler<Object>> createNotification(
            @RequestBody CreateNotificationRequest request) {
        return notificationService.createNotification(request);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponseHandler<Object>> getNotification() {
        return notificationService.getNotification();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseHandler<Object>> getNotificationById(
            @PathVariable Long id) {
        return notificationService.getNotificationById(id);
    }

    @PatchMapping("/{id}/enable")
    public ResponseEntity<ApiResponseHandler<Object>> enableNotification(
            @PathVariable Long id) {
        return notificationService.enableNotification(id);
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<ApiResponseHandler<Object>> disableNotification(
            @PathVariable Long id) {
        return notificationService.disableNotification(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseHandler<Object>> deleteNotification(
            @PathVariable Long id) {
        return notificationService.deleteNotification(id);
    }

    @PostMapping("/trigger")
    public ResponseEntity<ApiResponseHandler<Object>> triggerNotification(
            @RequestBody TriggerNotificationRequest request) {
        return notificationService.triggerNotification(request);
    }
}
