package org.owasp.wrongsecrets.canaries;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CanariesController {

    @Autowired
    CanaryCounter canaryCounter;

//https://canarytokens.org/history?token=s7cfbdakys13246ewd8ivuvku&auth=bc57423b5558e0deb8a2cf785e37f093
    @PostMapping(path = "/canaries/tokencallback", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> processCanaryToken(@RequestBody CanaryToken canaryToken) {
        try {
            String canarytokenContents = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(canaryToken);
            log.info("Canarytoken callback called with following token: {}", canarytokenContents);
            canaryCounter.upCallBackCounter();
            canaryCounter.setLastCanaryToken(canarytokenContents);
        } catch (JsonProcessingException e) {
            log.warn("Exception with processing canarytoken: {}", e.getMessage());
        }
        log.info("Canarytoken called, with manage_url {}", canaryToken.getManage_url());
        log.info("Total number of canary callback calls: {}", canaryCounter.getTotalCount());
        /*
        todo:
        - follow 3 of baeldung.com/spring-server-sent-events, but make sure you register the emitter per connection
        - and in a map lookup which emiter you can use for the given connection to send the event.
         */
        return new ResponseEntity<>("all good", HttpStatus.ACCEPTED);
    }

//http://canarytokens.com/terms/about/y0all60b627gzp19ahqh7rl6j/post.jsp
    @PostMapping(path = "/canaries/tokencallbackdebug", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> processCanaryTokendebug(@RequestBody String canarytokenContents) {
        canaryCounter.upCallBackCounter();
        canaryCounter.setLastCanaryToken(canarytokenContents);
        return new ResponseEntity<>("all good", HttpStatus.ACCEPTED);
    }
}
