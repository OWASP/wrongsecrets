package org.owasp.wrongsecrets.challenges;

import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.docker.Challenge29;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


public class ChallengeRestController {
    private final ScoreCard scoreCard;
    private final List<ChallengeUI> challenges;
    private final RuntimeEnvironment runtimeEnvironment;

    @Value("${hints_enabled}")
    private boolean hintsEnabled;
    @Value("${reason_enabled}")
    private boolean reasonEnabled;

    @Value("${ctf_enabled}")
    private boolean ctfModeEnabled;

    @Value("${ctf_key}")
    private String ctfKey;

    @Value("${challenge_acht_ctf_to_provide_to_host_value}")
    private String keyToProvideToHost;

    @Value("${CTF_SERVER_ADDRESS}")
    private String ctfServerAddress;

    public ChallengeRestController(ScoreCard scoreCard, List<ChallengeUI> challenges, RuntimeEnvironment runtimeEnvironment) {
        this.scoreCard = scoreCard;
        this.challenges = challenges;
        this.runtimeEnvironment = runtimeEnvironment;
    }
    @RestController
    public class secretKey {
        @GetMapping("/hidden")
        public String getChallengeSecret() {
            return "anyString";

        }
    }


}
