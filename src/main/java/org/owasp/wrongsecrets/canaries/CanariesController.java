package org.owasp.wrongsecrets.canaries;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CanariesController {

    @PostMapping(path = "/canaries/tokencallback", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> processCanaryToken(@RequestBody CanaryToken canaryToken) {
        try {
            String canarytokenContents = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(canaryToken);
            log.info("Canarytoken callback called with following token: {}", canarytokenContents);
        } catch (JsonProcessingException e) {
            log.warn("Exception with processing canarytoken: {}", e.getMessage());
        }
        log.info("Canarytoken called, with manage_url {}", canaryToken.getManageUrl());
        /*
        todo:
        - follow 3 of baeldung.com/spring-server-sent-events, but make sure you register the emitter per connection
        - and in a map lookup which emiter you can use for the given connection to send the event.
         */
        return new ResponseEntity<>("all good", HttpStatus.ACCEPTED);
    }
}
