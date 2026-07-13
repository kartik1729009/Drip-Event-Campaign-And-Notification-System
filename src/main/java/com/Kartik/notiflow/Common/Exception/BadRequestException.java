package com.Kartik.notiflow.Common.Exception;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message){
        super(message);
    }
    // includes original cause so stack trace is preserved when re-throwing
    public BadRequestException(String message, Throwable cause){
        super(message, cause);
    }
}
    