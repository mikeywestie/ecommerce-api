package com.mikey.ecommerce.common;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ProblemDetail handleApiException(ApiException ex) {
    ProblemDetail detail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    detail.setType(URI.create("https://api.ecommerce.local/errors/business-rule"));
    return detail;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(error -> error.getField() + " " + error.getDefaultMessage())
            .orElse("Validation failed");

    ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
    detail.setType(URI.create("https://api.ecommerce.local/errors/validation"));
    return detail;
  }

  @ExceptionHandler({
    OptimisticLockingFailureException.class,
    ObjectOptimisticLockingFailureException.class
  })
  public ProblemDetail handleOptimisticLocking(Exception ex) {
    ProblemDetail detail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, "Concurrent inventory update detected. Please retry the request.");
    detail.setType(URI.create("https://api.ecommerce.local/errors/concurrent-update"));
    return detail;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleUnexpected(Exception ex, HttpServletRequest request) {
    // Print the full stack trace to the Docker logs
    ex.printStackTrace();

    ProblemDetail detail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    detail.setType(URI.create("https://api.ecommerce.local/errors/internal-server-error"));
    detail.setInstance(URI.create(request.getRequestURI()));

    return detail;
  }
}
