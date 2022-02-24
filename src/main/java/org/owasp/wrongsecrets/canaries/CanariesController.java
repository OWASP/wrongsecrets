package org.owasp.wrongsecrets.canaries;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
public class CanariesController {

    @PostMapping(value="tokencallback")
    public void processCanaryToken(CanaryToken canaryToken){
        try {
            String canarytokenContents = new ObjectMapper().writeValueAsString(canaryToken);
            log.info("Canarytoken callback called with following token: {}", canarytokenContents);
        } catch (JsonProcessingException e) {
           log.warn("Exception with processing canarytoken: {}", e.getMessage());
        }
        log.info("Canarytoken called, with manage_url {}", canaryToken.manageUrl);
        /*
        todo:
        - follow 3 of baeldung.com/spring-server-sent-events, but make sure you register the emitter per connection
        - and in a map lookup which emiter you can use for the given connection to send the event.
         */
    }
}
