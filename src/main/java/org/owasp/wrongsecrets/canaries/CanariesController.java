package org.owasp.wrongsecrets.canaries;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Restcontroller used to accept calls from canarytokens.com */
@Slf4j
@RestController
public class CanariesController {

  @Autowired CanaryCounter canaryCounter;

  @PostMapping(path = "/canaries/tokencallback", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Callback method for canarytokens.com",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Required token",
              content = @Content(schema = @Schema(implementation = CanaryToken.class)),
              required = true))
  public ResponseEntity<String> processCanaryToken(@RequestBody @Valid CanaryToken canaryToken) {
    try {
      String canarytokenContents =
          new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(canaryToken);
      log.info("Canarytoken callback called with following token: {}", canarytokenContents);
      canaryCounter.upCallBackCounter();
      canaryCounter.setLastCanaryToken(canarytokenContents);
    } catch (JsonProcessingException e) {
      log.warn("Exception with processing canarytoken: {}", e.getMessage());
    }
    log.info("Canarytoken called, with manage_url {}", canaryToken.getManageUrl());
    log.info("Total number of canary callback calls: {}", canaryCounter.getTotalCount());
    return new ResponseEntity<>("all good", HttpStatus.ACCEPTED);
  }

  @PostMapping(path = "/canaries/tokencallbackdebug", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Callback method for canarytokens.com using unstructed data",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Required data",
              content = @Content(schema = @Schema(implementation = String.class)),
              required = true))
  public ResponseEntity<String> processCanaryTokendebug(@RequestBody String canarytokenContents) {
    canaryCounter.upCallBackCounter();
    canaryCounter.setLastCanaryToken(canarytokenContents);
    return new ResponseEntity<>("all good", HttpStatus.ACCEPTED);
  }
}
