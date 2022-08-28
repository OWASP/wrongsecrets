package org.owasp.wrongsecrets.challenges.docker;


import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.spongycastle.util.encoders.Base64;
import org.spongycastle.util.encoders.Hex;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Order(24)
public class Challenge24 extends Challenge {

    //code = https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/HMAC_SHA1.pdf
    private String hexCodeGroupByByte = "5361 6D706C65 206D6573 73616765 20666F72 206B6579 6C656E3D 626C6F63 6B6C656E";
    private String textAsUTF8 = "Sample message for keylen=blocklen";
    private String hmacKeyGroupByByte = "00010203 04050607 08090A0B 0C0D0E0F\n" +
        "10111213 14151617 18191A1B 1C1D1E1F 20212223 24252627\n" +
        "28292A2B 2C2D2E2F 30313233 34353637 38393A3B 3C3D3E3F";
    private String keyAsUtf8 = "!\"#$%&'()*+,-./0123456789:;<=>?";
    private String hmacREsult = "5FD596EE 78D5553C 8FF4E72D 266DFD19 2366DA29";


    public Challenge24(ScoreCard scoreCard) {
        super(scoreCard);
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(getActualData());
    }

    @Override
    public boolean answerCorrect(String answer) {
        log.info("challenge 23, actualdata: {}, answer: {}", getActualData(), answer);
        return getActualData().equals(answer);
    }

    @Override
    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(RuntimeEnvironment.Environment.DOCKER);
    }

    @Override
    public int difficulty() {
        return 3;
    }

    @Override
    public String getTech() {
        return "Cryptography";
    }

    public String getActualData() {
        return new String(Base64.decode(Hex.decode(Base64.decode("NTYzMjY4MzU1MTMyMzk3NDYyNTc1Njc1NjQ0ODRlNDI2MzMxNDI2ODYzMzM0ZTdhNjQzMjM5Nzk1YTQ1NDY3OTVhNTU0YTY4NWE0NDRkMzA0ZTU2Mzg2Yg=="))));

    }
}
