package com.Kartik.notiflow.MessageTemplate;

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
import com.Kartik.notiflow.MessageTemplate.Dto.MessageTemplateRequestDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/template")
public class MessageTemplateController {

    private final MessageTemplateService messageTemplateService;

    // create a new template or new version if name already exists
    @PostMapping("/create")
    public ResponseEntity<ApiResponseHandler<Object>> createTemplate(@RequestBody MessageTemplateRequestDto request) {
        return messageTemplateService.createTemplate(request);
    }

    // Manager only: activate a template by id
    @PatchMapping("/activate/{templateId}")
    public ResponseEntity<ApiResponseHandler<Object>> activateTemplate(@PathVariable Long templateId) {
        return messageTemplateService.activateTemplate(templateId);
    }

    // Manager only: update a template by id
    @PatchMapping("/update/{templateId}")
    public ResponseEntity<ApiResponseHandler<Object>> updateTemplate(
            @PathVariable Long templateId,
            @RequestBody MessageTemplateRequestDto request) {
        return messageTemplateService.updateTemplate(templateId, request);
    }

    // Manager only: delete a template by id
    @DeleteMapping("/delete/{templateId}")
    public ResponseEntity<ApiResponseHandler<Object>> deleteTemplate(@PathVariable Long templateId) {
        return messageTemplateService.deleteTemplate(templateId);
    }

    // get latest version of a template by name
    @GetMapping("/latest/{name}")
    public ResponseEntity<ApiResponseHandler<Object>> getLatestByName(@PathVariable String name) {
        return messageTemplateService.getLatestByName(name);
    }

    // get all versions of a template by name
    @GetMapping("/versions/{name}")
    public ResponseEntity<ApiResponseHandler<Object>> getAllVersionsByName(@PathVariable String name) {
        return messageTemplateService.getAllVersionsByName(name);
    }

    // get all templates in the workspace
    @GetMapping("/all")
    public ResponseEntity<ApiResponseHandler<Object>> getAllTemplates() {
        return messageTemplateService.getAllTemplates();
    }
}
