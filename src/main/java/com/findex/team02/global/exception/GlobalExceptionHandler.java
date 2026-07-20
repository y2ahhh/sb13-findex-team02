package com.findex.team02.global.exception;

import com.findex.team02.global.dto.ErrorResponse;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException exception
  ) {
    String details = exception.getBindingResult().getFieldErrors().stream()
        .findFirst()
        .map(fieldError -> fieldError.getDefaultMessage())
        .orElse("요청값을 확인해 주세요.");

    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        HttpStatus.BAD_REQUEST.value(),
        "잘못된 요청입니다.",
        details
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException exception
  ) {
    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        HttpStatus.BAD_REQUEST.value(),
        "잘못된 요청입니다.",
        exception.getMessage()
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler({
      MethodArgumentTypeMismatchException.class,
      HttpMessageNotReadableException.class
  })
  public ResponseEntity<ErrorResponse> handleRequestFormatException(
      Exception exception
  ) {
    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        HttpStatus.BAD_REQUEST.value(),
        "잘못된 요청입니다.",
        "요청값의 형식이 올바르지 않습니다."
    );

    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
      ResourceNotFoundException exception
  ) {
    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        HttpStatus.NOT_FOUND.value(),
        "요청한 리소스를 찾을 수 없습니다.",
        exception.getMessage()
    );

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(
      Exception exception
  ) {

    log.error("처리되지 않은 예외가 발생했습니다.", exception);

    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "서버 내부 오류가 발생했습니다.",
        "잠시 후 다시 시도해 주세요."
    );

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }

}
