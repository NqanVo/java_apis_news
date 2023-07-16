package com.javaspring.blogapi.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorValidationDTO {
    private LocalDateTime timestamp;
    private Map<String, String> messages;
    private String details;
}
