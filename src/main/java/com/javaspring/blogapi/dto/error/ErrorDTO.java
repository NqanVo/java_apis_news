package com.javaspring.blogapi.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorDTO {
    private LocalDateTime timestamp;
    private String message;
    private String details;
}
