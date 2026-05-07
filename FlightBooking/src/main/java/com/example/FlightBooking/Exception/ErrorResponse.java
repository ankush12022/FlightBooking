package com.example.FlightBooking.Exception;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {

    private String message;
    private int status;
    private LocalDateTime time;
    private Map<String, String> errors;

    public ErrorResponse(String message, int status, LocalDateTime time) {
        this.message = message;
        this.status = status;
        this.time = time;
    }

    public ErrorResponse(String message, int status, LocalDateTime time, Map<String, String> errors) {
        this.message = message;
        this.status = status;
        this.time = time;
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}
