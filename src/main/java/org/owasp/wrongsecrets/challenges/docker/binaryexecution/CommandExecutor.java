package org.owasp.wrongsecrets.challenges.docker.binaryexecution;


import java.io.*;


public abstract class CommandExecutor {
    protected File execFile;

    public CommandExecutor(File execFile) {
        this.execFile = execFile;
    }

    public abstract String executeCommand(String guess) throws IOException, InterruptedException;
}
