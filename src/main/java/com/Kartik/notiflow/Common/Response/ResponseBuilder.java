package com.Kartik.notiflow.Common.Response;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ResponseBuilder {

    public static <T> ApiResponseHandler<T> success(
            HttpStatus status,
            String message,
            T data) {

        return ApiResponseHandler.<T>builder()
                .success(true)
                .statusCode(status)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ApiResponseHandler<Object> error(
            HttpStatus status,
            String message) {

        return ApiResponseHandler.builder()
                .success(false)
                .statusCode(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}