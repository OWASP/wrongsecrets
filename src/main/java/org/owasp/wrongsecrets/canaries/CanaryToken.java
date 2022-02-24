package org.owasp.wrongsecrets.canaries;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CanaryToken {
    private final  String manageUrl;
    private final  String memo;
    private final  String channel;
    private final  String time;
    private final  AdditionalCanaryData additionalData;
}

