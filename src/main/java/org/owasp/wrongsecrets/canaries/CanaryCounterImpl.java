package org.owasp.wrongsecrets.canaries;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

/** Implementation of CanaryCounter using an Atomic integer for actual implementation. */
@Service
public class CanaryCounterImpl implements CanaryCounter {

  private static final AtomicInteger numberofCanaryCalls = new AtomicInteger(0);

  private String lastToken;

  @Override
  public void upCallBackCounter() {
    numberofCanaryCalls.incrementAndGet();
  }

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
