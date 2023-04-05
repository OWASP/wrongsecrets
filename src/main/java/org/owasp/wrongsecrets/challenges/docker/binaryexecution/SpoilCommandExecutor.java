package org.owasp.wrongsecrets.challenges.docker.binaryexecution;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SpoilCommandExecutor extends CommandExecutor {

    public SpoilCommandExecutor(File execFile) {
        super(execFile);
    }

    @Override
    public String executeCommand(String guess) throws IOException, InterruptedException {
        ProcessBuilder ps = new ProcessBuilder(execFile.getPath(), "spoil");
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
