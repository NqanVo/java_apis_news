package com.javaspring.blogapi.dto.error;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ErrorValidationDTO {
    private LocalDateTime timestamp;
    private Map<String, String> messages;
    private String details;

    public ErrorValidationDTO() {
    }

    public ErrorValidationDTO(LocalDateTime timestamp, Map<String, String> messages, String details) {
        this.timestamp = timestamp;
        this.messages = messages;
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
