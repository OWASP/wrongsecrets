package org.owasp.wrongsecrets.canaries;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AdditionalCanaryData {

    private final String srcIp;
    private final String useragent;
    private final String referer;
    private final String location;

}
