package org.owasp.wrongsecrets.challenges.docker.binaryexecution;

import static org.owasp.wrongsecrets.Challenges.ErrorResponses.EXECUTION_ERROR;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

/** Helper for classes to execute binaries as part of the Binary challenges. */
@Slf4j
public class BinaryExecutionHelper {

  private enum BinaryInstructionForFile {
    Spoil,
    Guess
  }

  public static final String ERROR_EXECUTION = EXECUTION_ERROR;
  private final int challengeNumber;

  private Exception executionException;

  private final MuslDetector muslDetector;

  public BinaryExecutionHelper(int challengeNumber, MuslDetector muslDetector) {
    this.challengeNumber = challengeNumber;
    this.muslDetector = muslDetector;
  }

  /**
   * Execute the command for the given binary with the actual guess for Golang only.
   *
   * @param guess containing the guess
   * @return the actual answer
   */
  public String executeGoCommand(String guess) {
    try {
      File execFile = createTempExecutable("wrongsecrets-golang");
      String result;
      if (Strings.isNullOrEmpty(guess)) {
        result = executeCommand(execFile, BinaryInstructionForFile.Spoil, "");
      } else {
        result = executeCommand(execFile, BinaryInstructionForFile.Guess, guess);
      }
      log.info(
          "stdout challenge {}: {}",
          challengeNumber,
          result.lines().collect(Collectors.joining("")));

      deleteFile(execFile);
      return result;
    } catch (Exception e) {
      log.warn("Error executing:", e);
      return ERROR_EXECUTION;
    }
  }

  /**
   * Execute the command for the given binary with the actual guess.
   *
   * @param guess containing the guess
   * @param fileName of the executable to be used (pre-defined, make sure it is never user input
   *     controlled)
   * @return the actual answer
   */
  public String executeCommand(String guess, String fileName) {
    BinaryInstructionForFile binaryInstructionForFile;
    if (Strings.isNullOrEmpty(guess)) {
      binaryInstructionForFile = BinaryInstructionForFile.Spoil;
    } else {
      binaryInstructionForFile = BinaryInstructionForFile.Guess;
    }
    try {
      File execFile = createTempExecutable(fileName);
      String result = executeCommand(execFile, binaryInstructionForFile, guess);
      deleteFile(execFile);
      log.info(
          "stdout challenge {}: {}",
          challengeNumber,
          result.lines().collect(Collectors.joining("")));
      return result;
    } catch (Exception e) {
      log.warn("Error executing:", e);
      executionException = e;
      return ERROR_EXECUTION;
    }
  }

  @SuppressFBWarnings(
      value = "COMMAND_INJECTION",
      justification = "We check for various injection methods and counter those")
  private String executeCommand(
      File execFile, BinaryInstructionForFile binaryInstructionForFile, String guess)
      throws IOException, InterruptedException {
    ProcessBuilder ps;

    if (!execFile.getPath().contains("wrongsecrets")
        || stringContainsCommandChainToken(execFile.getPath())
        || stringContainsCommandChainToken(guess)) {
      return BinaryExecutionHelper.ERROR_EXECUTION;
    }
    if (binaryInstructionForFile.equals(BinaryInstructionForFile.Spoil)) {
      ps = new ProcessBuilder(execFile.getPath(), "spoil");
    } else {
      if (execFile.getPath().contains("golang")) {
        ps = new ProcessBuilder(execFile.getPath(), "guess", guess);
      } else {
        ps = new ProcessBuilder(execFile.getPath(), guess);
      }
    }
    ps.redirectErrorStream(true);
    Process pr = ps.start();
    try (BufferedReader in =
        new BufferedReader(new InputStreamReader(pr.getInputStream(), StandardCharsets.UTF_8))) {
      String result = in.readLine();
      pr.waitFor();
      return result;
    }
  }

  private boolean stringContainsCommandChainToken(String testString) {
    String[] tokens = {"!", "&", "|", "<", ">", ";"};
    boolean found = false;
    for (String item : tokens) {
      if (testString.contains(item)) {
        found = true;
        break;
      }
    }
    return found;
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

  @SuppressFBWarnings(
      value = "PATH_TRAVERSAL_IN",
      justification = "The location of the file is hardcoded at the caller level")
  private File retrieveFile(String location) {
    try {
      log.info("First looking at location:'classpath:executables/{}'", location);
      return ResourceUtils.getFile("classpath:executables/" + location);
    } catch (FileNotFoundException e) {
      log.debug("exception finding file", e);
      log.info(
          "You might be running this in a docker container, trying alternative path:"
              + " '/home/wrongsecrets/{}'",
          location);
      return new File("/home/wrongsecrets/" + location);
    }
  }

  private boolean useMusl() {
    return muslDetector.isMusl();
  }

  @SuppressFBWarnings(
      value = "PATH_TRAVERSAL_IN",
      justification = "The location of the fileName is hardcoded at the caller level")
  private File createTempExecutable(String fileName) throws IOException {
    if (useWindows()) {
      fileName = fileName + "-windows.exe";
      log.info("While we detected windows, please note that it is officially not supported.");
    } else if (useLinux()) {
      fileName = fileName + "-linux";
      if (useMusl() && !fileName.contains("golang")) {
        fileName = fileName + "-musl";
      }
    }
    if (!useX86()) {
      if (!useArm()) {
        log.info(
            "We found a different system architecture than x86-amd64 or aarch64. Will default to"
                + " aarch64.");
      }
      fileName = fileName + "-arm";
    }
    File challengeFile = retrieveFile(fileName);
    // prepare file to execute
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
