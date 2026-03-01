package org.owasp.wrongsecrets;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * Global exception handler for REST API endpoints. Returns RFC 9457-style {@link ProblemDetail}
 * responses. Scoped to {@link RestController} annotated beans only; Thymeleaf controllers are
 * unaffected.
 */
@RestControllerAdvice(annotations = RestController.class)
public class ApiExceptionAdvice {

  /**
   * Handles {@link ResponseStatusException} thrown from REST controllers and maps it to an RFC
   * 9457-compliant {@link ProblemDetail} response.
   *
   * @param ex the exception to handle
   * @param request the current HTTP request
   * @return a {@link ProblemDetail} with status, title, detail and instance populated
   */
  @ExceptionHandler(ResponseStatusException.class)
  public ProblemDetail handleResponseStatus(
      ResponseStatusException ex, HttpServletRequest request) {
    ProblemDetail pd = ProblemDetail.forStatus(ex.getStatusCode());
    pd.setTitle(
        ex.getReason() != null ? ex.getReason() : ex.getStatusCode().toString());
    pd.setDetail(ex.getMessage());
    pd.setInstance(URI.create(request.getRequestURI()));
    return pd;
  }

  /**
   * Handles unexpected exceptions thrown from REST controllers and maps them to an RFC 9457-
   * compliant {@link ProblemDetail} response with HTTP 500 status.
   *
   * @param ex the exception to handle
   * @param request the current HTTP request
   * @return a {@link ProblemDetail} with status 500, title and detail populated
   */
  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGenericException(Exception ex, HttpServletRequest request) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    pd.setTitle("Internal Server Error");
    pd.setDetail(ex.getMessage());
    pd.setInstance(URI.create(request.getRequestURI()));
    return pd;
  }
}
