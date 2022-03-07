package org.owasp.wrongsecrets.canaries;

public interface CanaryCounter {

    void upCallBackCounter();

    int getTotalCount();

    void setLastCanaryToken(String tokenContent);

    String getLastToken();

}
