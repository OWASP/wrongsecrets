package org.owasp.wrongsecrets.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

/** Controller used for one of the front-end challenges. */
@Slf4j
@Controller
public class TokenController {

  private final String dockerMountPath;

  public TokenController(@Value("${challengedockermtpath}") String dockerMountPath) {
    this.dockerMountPath = dockerMountPath;
  }

  @Operation(summary = "Endpoint for interaction at challenge 16")
  @PostMapping(
      path = "/token",
      consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
  public ResponseEntity<?> clientCredentialToken(TokenRequest tokenRequest) {
    if ("client_credentials".equals(tokenRequest.grant_type())
        && "WRONGSECRET_CLIENT_ID".equals(tokenRequest.client_id())
        && getActualData().equals(tokenRequest.client_secret())) {
      return ResponseEntity.ok(
          new TokenResponse(UUID.randomUUID().toString(), "bearer", 54321L, "user_info"));
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  /**
   * TokenRequest used to call tokenController.
   *
   * @param grant_type string for grant type
   * @param client_id string for the clientid
   * @param client_secret holding the client secret for auth. target of challenge.
   */
  public record TokenRequest(String grant_type, String client_id, String client_secret) {}

  /**
   * TokenResponse returned by TokenController.
   *
   * @param accessToken string with the token retrieved through oauth
   * @param tokenType string with the tokentype retrieved through oauth
   * @param expiresIn long with the token expiration moment
   * @param scope string with the token scope
   */
  public record TokenResponse(
      @JsonProperty("access_token") String accessToken,
      @JsonProperty("token_type") String tokenType,
      @JsonProperty("expires_in") Long expiresIn,
      String scope) {}

  @SuppressFBWarnings(
      value = "PATH_TRAVERSAL_IN",
      justification = "The location of the dockerMountPath is based on an Env Var")
  private String getActualData() {
    try {
      return Files.readString(Paths.get(dockerMountPath, "secondkey.txt"), StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.warn("Exception during file reading, defaulting to default without cloud environment", e);
      return "if_you_see_this_please_use_docker_instead";
    }
  }
}
