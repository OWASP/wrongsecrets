package org.owasp.wrongsecrets.challenges.docker.binaryexecution;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class GuessCommandExecutor extends CommandExecutor {
    private String guess;

    public GuessCommandExecutor(File execFile, String guess) {
        super(execFile);
        this.guess = guess;
    }

    @Override
    public String executeCommand(String guess) throws InterruptedException {
        ProcessBuilder ps;
        if (execFile.getPath().contains("golang")) {
            ps = new ProcessBuilder(execFile.getPath(), "guess", guess);
        } else {
            ps = new ProcessBuilder(execFile.getPath(), guess);
        }
        ps.redirectErrorStream(true);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(ps.start().getInputStream(), StandardCharsets.UTF_8))) {
            String result = in.readLine();
            ps.start().waitFor();
            return result;
        } catch (IOException e) {
            return BinaryExecutionHelper.ERROR_EXECUTION;
        }
    }
}
