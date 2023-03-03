package org.owasp.wrongsecrets.challenges.docker.binaryexecution;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class MuslDetectorImpl implements MuslDetector {


    @Override
    public boolean isMusl() {
        ProcessBuilder ps = new ProcessBuilder("ldd", "/bin/ls");
        ps.redirectErrorStream(true);
        try {
            Process pr = ps.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String result = in.readLine();
            return result.contains("musl");
        } catch (IOException e) {
            log.error("Could not detect musl due to: ", e);
            return false;
        }

    }
}
