package com.javaspring.blogapi.exception;

import com.javaspring.blogapi.dto.error.ErrorDTO;
import com.javaspring.blogapi.dto.error.ErrorValidationDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.NoSuchFileException;
import java.time.LocalDateTime;
import java.util.*;

@ControllerAdvice
public class CustomException extends ResponseEntityExceptionHandler {
    // * Xử lý các lỗi còn lại
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorDTO> handleAllException(Exception ex, WebRequest request) throws Exception {
        ErrorDTO errorDTO = new ErrorDTO(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<ErrorDTO>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // * Xử lý lỗi không tìm thấy
    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<ErrorDTO> handleNotFoundException(Exception ex, WebRequest request) throws Exception {
        ErrorDTO errorDetails = new ErrorDTO(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<ErrorDTO>(errorDetails, HttpStatus.NOT_FOUND);
    }
    // * Xử lý lỗi không được trống file
    @ExceptionHandler(MultipartException.class)
    public final ResponseEntity<ErrorDTO> handleNotEmptyFileException(Exception ex, WebRequest request) throws Exception {
        ErrorDTO errorDetails = new ErrorDTO(
                LocalDateTime.now(),
                "File Không được trống",
                request.getDescription(false));
        return new ResponseEntity<ErrorDTO>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    // * Xử lý lỗi kích thước file tối đa
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public final ResponseEntity<ErrorDTO> handleMaxSizeFileException(Exception ex, WebRequest request) throws Exception {
        ErrorDTO errorDetails = new ErrorDTO(
                LocalDateTime.now(),
                "Kích thước file tối đa là 10MB",
                request.getDescription(false));
        return new ResponseEntity<ErrorDTO>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    // * Xử lý lỗi tài khoản chưa kích hoạt
    @ExceptionHandler(DisabledException.class)
    public final ResponseEntity<ErrorDTO> handleDisabledException(Exception ex, WebRequest request) throws Exception {
        ErrorDTO errorDetails = new ErrorDTO(
                LocalDateTime.now(),
                "Tài khoản chưa kích hoạt, vui lòng kiểm tra email",
                request.getDescription(false));
        return new ResponseEntity<ErrorDTO>(errorDetails, HttpStatus.UNAUTHORIZED);
    }
    // * Xử lý lỗi không tìm thấy ảnh
    @ExceptionHandler(NoSuchFileException.class)
    public final ResponseEntity<ErrorDTO> handleNotFoundFileException(Exception ex, WebRequest request) throws Exception {
        ErrorDTO errorDetails = new ErrorDTO(
                LocalDateTime.now(),
                "Không tìm thấy ảnh",
                request.getDescription(false));
        return new ResponseEntity<ErrorDTO>(errorDetails, HttpStatus.NOT_FOUND);
    }
    // * Xử lý lỗi về yêu cầu người dùng
    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<ErrorDTO> handleBadRequestException(Exception ex, WebRequest request) throws Exception {
        ErrorDTO errorDetails = new ErrorDTO(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<ErrorDTO>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // * Xử lý lỗi về không có quyền
    @ExceptionHandler(UnauthorizedException.class)
    public final ResponseEntity<ErrorDTO> handleUnauthorizedException(Exception ex, WebRequest request) throws Exception {
        ErrorDTO errorDetails = new ErrorDTO(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<ErrorDTO>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    // * Xử lý lỗi về không có quyền (httpSecurity)
    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<ErrorDTO> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDTO(LocalDateTime.now(), "Không có quyền truy cập", ex.getMessage()));
    }
    // * Xử lý lỗi khi xóa 1 bảng còn ràn buộc
//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public final ResponseEntity<ErrorDTO> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) throws Exception {
//        ErrorDTO errorDetails = new ErrorDTO(
//                LocalDateTime.now(),
//                ex.getMessage(),
//                request.getDescription(false));
//        return new ResponseEntity<ErrorDTO>(errorDetails, HttpStatus.UNAUTHORIZED);
//    }

    // * Xử lý lỗi về validation input
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String key = ((FieldError) error).getField();
            String value = error.getDefaultMessage();
            errors.put(key, value);
        });
        ErrorValidationDTO errorValidationDTO = new ErrorValidationDTO(
                LocalDateTime.now(),
                errors,
                request.getDescription(false)
        );
        return new ResponseEntity(errorValidationDTO, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }
}


