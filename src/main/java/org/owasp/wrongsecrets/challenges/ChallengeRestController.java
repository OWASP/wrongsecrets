package org.owasp.wrongsecrets.challenges;
import io.swagger.v3.oas.annotations.Hidden;
import org.owasp.wrongsecrets.challenges.docker.Challenge29;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Controller;


@Controller
@RestController
public class ChallengeRestController {
    private final Challenge29 challenge29;

    public ChallengeRestController(Challenge29 challenge29) {
        this.challenge29 = challenge29;
    }

    @GetMapping("/hidden")
    @Hidden
    public String getChallengeSecret(){return challenge29.spoiler().solution();}

}
