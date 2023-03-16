package org.owasp.wrongsecrets;

import org.springframework.boot.ExitCodeGenerator;

public class FailtoStartupException extends RuntimeException implements ExitCodeGenerator {

    public FailtoStartupException(String message) {
        super(message);
    }

    @Override
    public int getExitCode() {
        return 1;
    }
}
