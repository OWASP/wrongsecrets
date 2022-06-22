package org.owasp.wrongsecrets.challenges.docker;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

import java.io.*;

@Slf4j
public class BinaryExecutionHelper {


    public static String ERROR_EXECUTION = "Error with executing";
    private final int challengeNumber;

    public BinaryExecutionHelper(int challengeNumber) {
        this.challengeNumber = challengeNumber;
    }

    private boolean useX86() {
        String systemARch = System.getProperty("os.arch");
        log.info("System arch detected: {}", systemARch);
        return systemARch.contains("amd64") || systemARch.contains("x86");
    }

    private boolean useLinux() {
        String systemARch = System.getProperty("os.arch");
        log.info("System arch detected: {}", systemARch);
        return systemARch.contains("amd64");
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

    private File createTempExecutable(String fileName) throws IOException {
        File challengeFile;
        if (useX86()) {
            challengeFile = retrieveFile(fileName);
            if (useLinux()) {
                challengeFile = retrieveFile(fileName + "linux");
            }
        } else {
            challengeFile = retrieveFile(fileName + "-c-arm");
        }
        //prepare file to execute
        File execFile = File.createTempFile("c-exec-" + fileName, "sh");
        if (!execFile.setExecutable(true)) {
            log.info("setting the file {} executable failed... rest can be ignored", execFile.getPath());
        }
        OutputStream os = new FileOutputStream(execFile.getPath());
        ByteArrayInputStream is = new ByteArrayInputStream(FileUtils.readFileToByteArray(challengeFile));
        byte[] b = new byte[2048];
        int length;
        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }
        is.close();
        os.close();

        return execFile;
    }

    private String executeCommand(File execFile, String argument) throws IOException, InterruptedException {
        ProcessBuilder ps = new ProcessBuilder(execFile.getPath(), argument);
        ps.redirectErrorStream(true);
        Process pr = ps.start();
        BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String result = in.readLine();
        pr.waitFor();
        return result;
    }


    public String executeCommand(String guess, String fileName) {
        if (Strings.isNullOrEmpty((guess))) {
            guess = "spoil";
        }
        try {
            File execFile = createTempExecutable(fileName);
            String result = executeCommand(execFile, guess);
            if (!execFile.delete()) {
                log.info("Deleting the file {} failed...", execFile.getPath());
            }
            log.info("stdout challenge {}: {}", challengeNumber, result);
            return result;
        } catch (IOException | NullPointerException | InterruptedException e) {
            log.warn("Error executing:", e);
            return ERROR_EXECUTION;
        }

    }
}
