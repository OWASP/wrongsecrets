package org.owasp.wrongsecrets.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@Controller
public class TokenController {

    @PostMapping(path = "/oauth/token", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<?> clientCredentialToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                                   TokenRequest tokenRequest) {
        if ("Basic am9objp3ZzJRSWsyTjhZbVRFZDNDL2pubGtGR2VJcGRlR0krbEt6SzdyUk9lUFlVPQ==".equals(authorization)
            && "client_credentials".equals(tokenRequest.grant_type())
            && "user_info".equals(tokenRequest.scope())) {
            return ResponseEntity.ok(
                new TokenResponse(UUID.randomUUID().toString(), "bearer", 54321L, "user_info")
            );
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .build();
    }

    public record TokenRequest(String grant_type,
                               String scope) {
    }

    public record TokenResponse(@JsonProperty("access_token") String accessToken,
                                @JsonProperty("token_type") String tokenType,
                                @JsonProperty("expires_in") Long expiresIn,
                                String scope) {
    }
}
