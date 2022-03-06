package org.owasp.wrongsecrets.canaries;

public interface CanaryCounter{

    public abstract void upCallBackCounter();
    public abstract int getTotalCount();

}
