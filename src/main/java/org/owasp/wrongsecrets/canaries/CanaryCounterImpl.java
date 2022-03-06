package org.owasp.wrongsecrets.canaries;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;


@Service
public class CanaryCounterImpl implements CanaryCounter {

    private static AtomicInteger numberofCanaryCalls = new AtomicInteger(0);


    @Override
    public void upCallBackCounter() {
        numberofCanaryCalls.incrementAndGet();
    }

    // accessed via ajax loop (and controller), if value changes update display
    @Override
    public int getTotalCount() {
        return numberofCanaryCalls.get();
    }
}


