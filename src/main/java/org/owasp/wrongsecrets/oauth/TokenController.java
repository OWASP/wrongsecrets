package org.owasp.wrongsecrets.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Controller
public class TokenController {

    private final String dockerMountPath;

    public TokenController(@Value("${challengedockermtpath}") String dockerMountPath) {
        this.dockerMountPath = dockerMountPath;
    }


    @Operation(summary = "Endpoint for interaction at challenge 16")
    @PostMapping(path = "/token", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<?> clientCredentialToken(TokenRequest tokenRequest) {
        if ("client_credentials".equals(tokenRequest.grant_type())
            && "WRONGSECRET_CLIENT_ID".equals(tokenRequest.client_id())
            && getActualData().equals(tokenRequest.client_secret())) {
            return ResponseEntity.ok(
                new TokenResponse(UUID.randomUUID().toString(), "bearer", 54321L, "user_info")
            );
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .build();
    }

    /**
     * Tokenrequest
     *
     * @param grant_type    ew
     * @param client_id     we
     * @param client_secret we
     */
    public record TokenRequest(String grant_type,
                               String client_id,
                               String client_secret) {
    }

    public record TokenResponse(@JsonProperty("access_token") String accessToken,
                                @JsonProperty("token_type") String tokenType,
                                @JsonProperty("expires_in") Long expiresIn,
                                String scope) {
    }

    @SuppressFBWarnings(value = "PATH_TRAVERSAL_IN", justification = "The location of the dockerMountPath is based on an Env Var")
    public String getActualData() {
        try {
            return Files.readString(Paths.get(dockerMountPath, "secondkey.txt"));
        } catch (Exception e) {
            log.warn("Exception during file reading, defaulting to default without cloud environment", e);
            return "if_you_see_this_please_use_docker_instead";
        }
    }
}
