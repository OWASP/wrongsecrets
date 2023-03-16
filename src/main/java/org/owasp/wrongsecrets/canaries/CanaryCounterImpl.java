package org.owasp.wrongsecrets.canaries;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;


@Service
public class CanaryCounterImpl implements CanaryCounter {

    private static final AtomicInteger numberofCanaryCalls = new AtomicInteger(0);

    private String lastToken;


    @Override
    public void upCallBackCounter() {
        numberofCanaryCalls.incrementAndGet();
    }

    // accessed via ajax loop (and controller), if value changes update display
    @Override
    public int getTotalCount() {
        return numberofCanaryCalls.get();
    }

    @Override
    public void setLastCanaryToken(String tokenContent) {
        lastToken = tokenContent;
    }

    @Override
    public String getLastToken() {
        return lastToken;
    }
}
