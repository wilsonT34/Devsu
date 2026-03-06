package com.banco.clientepersona.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
/**
 *
 * @author user
 */
@Data
@AllArgsConstructor
public class ErrorResponseDTO {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
