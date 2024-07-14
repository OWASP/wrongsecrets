package org.owasp.wrongsecrets.challenges.docker;

import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.stereotype.Component;

/** This challenge is about using a publicly specified key to safeguard data. */
@Slf4j
@Component
public class Challenge24 extends FixedAnswerChallenge {

  private String getActualData() {
    return new String(
        Hex.decode(
            "3030303130323033203034303530363037203038303930413042203043304430453046203130313131323133203134313531363137203138313931413142203143314431453146203230323132323233203234323532363237203238323932413242203243324432453246203330333133323333203334333533363337203338333933413342203343334433453346"
                .getBytes(StandardCharsets.UTF_8)),
        StandardCharsets.UTF_8);
  }

  @Override
  public String getAnswer() {
    return getActualData();
  }
}
