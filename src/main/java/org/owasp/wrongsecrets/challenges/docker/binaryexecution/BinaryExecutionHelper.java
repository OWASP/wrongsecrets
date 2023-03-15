package org.owasp.wrongsecrets.challenges.docker.binaryexecution;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;


@Slf4j
public class BinaryExecutionHelper {


    public static final String ERROR_EXECUTION = "Error with executing";
    private final int challengeNumber;

    private Exception executionException;

    private final MuslDetector muslDetector;

    public BinaryExecutionHelper(int challengeNumber, MuslDetector muslDetector) {
        this.challengeNumber = challengeNumber;
        this.muslDetector = muslDetector;
    }

    public String executeGoCommand(String guess) {
        try {
            File execFile = createTempExecutable("wrongsecrets-golang");
            String result;
            if (Strings.isNullOrEmpty(guess)) {
                result = executeCommand(execFile, "spoil");
            } else {
                result = executeCommand(execFile, "guess", guess);
            }
            log.info("stdout challenge {}: {}", challengeNumber, result);

            deleteFile(execFile);
            return result;
        } catch (Exception e) {
            log.warn("Error executing:", e);
            return ERROR_EXECUTION;
        }
    }

    public String executeCommand(String guess, String fileName) {
        if (Strings.isNullOrEmpty((guess))) {
            guess = "spoil";
        }
        try {
            File execFile = createTempExecutable(fileName);
            String result = executeCommand(execFile, guess);
            deleteFile(execFile);
            log.info("stdout challenge {}: {}", challengeNumber, result);
            return result;
        } catch (Exception e) {
            log.warn("Error executing:", e);
            executionException = e;
            return ERROR_EXECUTION;
        }

    }

    private String executeCommand(File execFile, String argument, String argument2) throws IOException, InterruptedException {
        ProcessBuilder ps;
        if (Strings.isNullOrEmpty(argument2)) {
            ps = new ProcessBuilder(execFile.getPath(), argument);
        } else {
            ps = new ProcessBuilder(execFile.getPath(), argument, argument2);
        }
        ps.redirectErrorStream(true);
        Process pr = ps.start();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream(), StandardCharsets.UTF_8))) {
            String result = in.readLine();
            pr.waitFor();
            return result;
        }
    }

    private String executeCommand(File execFile, String argument) throws IOException, InterruptedException {
        return executeCommand(execFile, argument, "");
    }

    @VisibleForTesting
    public Exception getExecutionException() {
        return executionException;
    }

    private boolean useX86() {
        String systemARch = System.getProperty("os.arch");
        log.info("System arch detected: {}", systemARch);
        return systemARch.contains("amd64") || systemARch.contains("x86");
    }

    private boolean useArm() {
        String systemARch = System.getProperty("os.arch");
        log.info("System arch detected: {}", systemARch);
        return systemARch.contains("aarch64");
    }

    private boolean useLinux() {
        String osName = System.getProperty("os.name").toLowerCase();
        log.info("System arch detected: {}", osName);
        return osName.contains("nix") || osName.contains("nux") || osName.contains("aix");
    }

    private boolean useWindows() {
        String systemARch = System.getProperty("os.arch");
        log.info("System arch detected: {}", systemARch);
        String osName = System.getProperty("os.name");
        log.info("OS Name detected: {}", osName);
        return systemARch.contains("amd64") && osName.toLowerCase().contains("windows");
    }

    private File retrieveFile(String location) {
        try {
            log.info("First looking at location:'classpath:executables/{}'", location);
            return ResourceUtils.getFile("classpath:executables/" + location);
        } catch (FileNotFoundException e) {
            log.debug("exception finding file", e);
            log.info("You might be running this in a docker container, trying alternative path: '/home/wrongsecrets/{}'", location);
            return new File("/home/wrongsecrets/" + location);
        }
    }

    private boolean useMusl() {
        return muslDetector.isMusl();
    }

    private File createTempExecutable(String fileName) throws IOException {
        if (useWindows()) {
            fileName = fileName + "-windows.exe";
            log.info("While we detected windows, please note that it is officially not supported.");
        } else if (useLinux()) {
            fileName = fileName + "-linux";
            if (useMusl()) {
                fileName = fileName + "-musl";
            }
        }
        if (!useX86()) {
            if (!useArm()) {
                log.info("We found a different system architecture than x86-amd64 or aarch64. Will default to aarch64.");
            }
            fileName = fileName + "-arm";
        }
        File challengeFile = retrieveFile(fileName);
        //prepare file to execute
        File execFile = File.createTempFile("c-exec-" + fileName, "sh");
        if (!execFile.setExecutable(true)) {
            log.info("setting the file {} executable failed... rest can be ignored", execFile.getPath());
        }
        FileUtils.copyFile(challengeFile, execFile);
        return execFile;
    }

    private void deleteFile(File execFile) {
        if (!execFile.delete()) {
            log.info("Deleting the file {} failed...", execFile.getPath());
        }
    }
}
