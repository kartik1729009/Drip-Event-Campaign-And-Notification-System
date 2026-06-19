package com.Kartik.notiflow.Common.Response;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ApiResponseHandler<T> {
    private Boolean success;
    private HttpStatus statusCode;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    

}
