package dev.hireben.demo.common_libs.http.handler;

import java.net.SocketTimeoutException;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import dev.hireben.demo.common_libs.exception.ApplicationException;
import dev.hireben.demo.common_libs.exception.InsufficientPermissionException;
import dev.hireben.demo.common_libs.http.dto.HttpFieldValidationErrorMap;
import dev.hireben.demo.common_libs.jwt.exception.TokenIssuanceFailException;
import dev.hireben.demo.common_libs.jwt.exception.TokenMalformedException;
import io.jsonwebtoken.ClaimJwtException;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.validation.ConstraintViolationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class HttpGlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private final Tracer tracer;

  // =============================================================================

  protected static final Map<Class<? extends Throwable>, HttpStatus> exceptionStatusMap = new HashMap<>(
      Map.of(InsufficientPermissionException.class, HttpStatus.FORBIDDEN,
          TokenMalformedException.class, HttpStatus.UNAUTHORIZED,
          TokenIssuanceFailException.class, HttpStatus.BAD_REQUEST));

  // =============================================================================

  @Override
  protected final ResponseEntity<Object> createResponseEntity(
      Object body,
      HttpHeaders headers,
      HttpStatusCode statusCode,
      WebRequest request) {

    if (body instanceof ProblemDetail problemDetail) {
      problemDetail.setProperty("timestamp", Instant.now());
      TraceContext context = tracer.currentTraceContext().context();
      problemDetail.setProperty("trace", context != null ? context.traceId() : "");
    }

    return super.createResponseEntity(body, headers, statusCode, request);
  }

  // =============================================================================

  @Override
  protected final ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    ProblemDetail problemDetail = ex.updateAndGetBody(getMessageSource(), LocaleContextHolder.getLocale());

    Collection<HttpFieldValidationErrorMap> errors = ex.getBindingResult().getAllErrors().stream()
        .map(error -> HttpFieldValidationErrorMap.builder()
            .field(((FieldError) error).getField())
            .message(error.getDefaultMessage())
            .build())
        .toList();

    problemDetail.setProperty("errors", errors);

    return createResponseEntity(problemDetail, headers, status, request);
  }

  // -----------------------------------------------------------------------------

  @ExceptionHandler(ConstraintViolationException.class)
  private ResponseEntity<Object> handleConstraintViolation(
      ConstraintViolationException ex,
      WebRequest request) {

    HttpStatus status = HttpStatus.BAD_REQUEST;

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, "Validation failed");

    Collection<HttpFieldValidationErrorMap> errors = ex.getConstraintViolations().stream()
        .map(error -> HttpFieldValidationErrorMap.builder()
            .field(error.getPropertyPath().toString())
            .message(error.getMessage())
            .build())
        .toList();

    problemDetail.setProperty("errors", errors);

    return createResponseEntity(problemDetail, HttpHeaders.EMPTY, status, request);
  }

  // -----------------------------------------------------------------------------

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  private ResponseEntity<Object> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex,
      WebRequest request) {

    HttpStatus status = HttpStatus.BAD_REQUEST;

    String message = String.format("Invalid parameter value: %s", ex.getName());

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);

    return createResponseEntity(problemDetail, HttpHeaders.EMPTY, status, request);
  }

  // -----------------------------------------------------------------------------

  @ExceptionHandler(MissingRequestHeaderException.class)
  private ResponseEntity<Object> handleMissingRequestHeader(
      MissingRequestHeaderException ex,
      WebRequest request) {

    HttpStatus status = HttpStatus.BAD_REQUEST;

    String message = String.format("Missing HTTP header: %s", ex.getHeaderName());

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);

    return createResponseEntity(problemDetail, HttpHeaders.EMPTY, status, request);
  }

  // -----------------------------------------------------------------------------

  @ExceptionHandler(SocketTimeoutException.class)
  private ResponseEntity<Object> handleSocketTimeout(
      SocketTimeoutException ex,
      WebRequest request) {

    HttpStatus status = HttpStatus.GATEWAY_TIMEOUT;

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, "Gateway timed out");

    return createResponseEntity(problemDetail, HttpHeaders.EMPTY, status, request);
  }

  // -----------------------------------------------------------------------------

  @ExceptionHandler(ClaimJwtException.class)
  private ResponseEntity<Object> handleJwtVerificationFailure(
      ClaimJwtException ex,
      WebRequest request) {

    HttpStatus status = HttpStatus.UNAUTHORIZED;

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());

    return createResponseEntity(problemDetail, HttpHeaders.EMPTY, status, request);
  }

  // -----------------------------------------------------------------------------

  @ExceptionHandler(ApplicationException.class)
  private ResponseEntity<Object> handleApplicationException(
      ApplicationException ex,
      WebRequest request) {

    HttpStatus status = exceptionStatusMap.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());

    return createResponseEntity(problemDetail, HttpHeaders.EMPTY, status, request);
  }

  // -----------------------------------------------------------------------------

  @ExceptionHandler(Exception.class)
  ResponseEntity<Object> catchAllException(
      Exception ex,
      WebRequest request) {

    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status,
        "An unhandled error occured at the server side");

    logger.error("Unhandled exception caught", ex);

    return createResponseEntity(problemDetail, HttpHeaders.EMPTY, status, request);
  }

}
