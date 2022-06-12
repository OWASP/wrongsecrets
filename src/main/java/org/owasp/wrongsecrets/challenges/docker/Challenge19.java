package org.owasp.wrongsecrets.challenges.docker;


import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.DOCKER;

@Component
@Order(19)
@Slf4j
public class Challenge19 extends Challenge {

    public static String ERROR_EXECUTION = "Error with executing";

    public Challenge19(ScoreCard scoreCard) {
        super(scoreCard);
    }


    @Override
    public Spoiler spoiler() {
        return new Spoiler(executeCommand(""));
    }

    @Override
    public boolean answerCorrect(String answer) {
        return executeCommand(answer).equals("This is correct! Congrats!");
    }

    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(DOCKER);
    }


    private boolean useX86() {
        String systemARch = System.getProperty("os.arch");
        log.info("System arch detected: {}", systemARch);
        return systemARch.contains("x86_64") || systemARch.contains("amd64");
    }

    private File createTempExecutable() throws IOException {
        File challengeFile;
        if (useX86()) {
            challengeFile = ResourceUtils.getFile("classpath:wrongsecrets-c");
        } else {
            challengeFile = ResourceUtils.getFile("classpath:wrongsecrets-c-arm");
        }
        //prepare file to execute
        File execfile = File.createTempFile("c-exec-challenge19", "sh");
        OutputStream os = new FileOutputStream(execfile.getPath());
        ByteArrayInputStream is = new ByteArrayInputStream(FileUtils.readFileToByteArray(challengeFile));
        byte[] b = new byte[2048];
        int length;
        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }
        is.close();
        os.close();
        execfile.setExecutable(true);
        return execfile;
    }

    private String executeCommand(File execFile, String argument) throws IOException, InterruptedException {
        ProcessBuilder ps = new ProcessBuilder(execFile.getPath(), argument);
        ps.redirectErrorStream(true);
        Process pr = ps.start();
        BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line = "";
        String result = "";
        while ((line = in.readLine()) != null) {
            result = result + line;
        }
        pr.waitFor();
        return result;
    }


    private String executeCommand(String guess) {
        Runtime runTime = Runtime.getRuntime();
        if (Strings.isNullOrEmpty((guess))) {
            guess = "spoil";
        }
        try {
            File execfile = createTempExecutable();
            String result = executeCommand(execfile, guess);
            execfile.delete();
            log.info("stdout challenge 19: {}", result);
            return result;
        } catch (IOException | NullPointerException | InterruptedException e) {
            log.warn("Error executing:", e);
            return ERROR_EXECUTION;
        }

    }
}
