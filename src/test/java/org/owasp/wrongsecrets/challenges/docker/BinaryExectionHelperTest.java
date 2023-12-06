package org.owasp.wrongsecrets.challenges.docker;

import org.assertj.core.util.Strings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.BinaryExecutionHelper;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.MuslDetector;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.MuslDetectorImpl;

class BinaryExectionHelperTest {

  private String osArch, osName;

  @BeforeEach
  void cacheSystemProperties() {
    osArch = System.getProperty("os.arch");
    osName = System.getProperty("os.name");
  }

  @Test
  void executeOnWindows() {
    if (!Strings.isNullOrEmpty(osName) && osName.toLowerCase().contains("windows")) {
      return; // no need to test this here as you are running on windows ;-).
    }
    executionHelper("Windows", "amd64", "/test-windows.exe", new MuslDetectorImpl());
  }

  @Test
  void executeOnLinuxAmd64() {
    if (osName.toLowerCase().contains("nix") || osName.toLowerCase().contains("lin")) {
      return; // no need to test this here as you are running on linux ;-).
    }
    executionHelper("linux", "amd64", "test-linux", new MuslDetectorImpl());
  }

  @Test
  void executeOnLinuxARM() {
    if (osName.toLowerCase().contains("nix") || osName.toLowerCase().contains("lin")) {
      return; // no need to test this here as you are running on linux ;-).
    }
    executionHelper("linux", "aarch64", "test-linux-arm", new MuslDetectorImpl());
  }

  @Test
  void executeOnMacOS() {
    if (osName.toLowerCase().contains("mac") && osArch.equalsIgnoreCase("aarch64")) {
      return; // no need to test this here as you are running on macOS ;-).
    }
    executionHelper("Mac OS X", "aarch64", "test-arm", new MuslDetectorImpl());
  }

  @Test
  void executeOnMacOSARM() {
    if (osName.toLowerCase().contains("mac") && osArch.toLowerCase().equals("x86_64")) {
      return; // no need to test this here as you are running on macOS ;-).
    }
    executionHelper("Mac OS X", "x86_64", "test", new MuslDetectorImpl());
  }

  @Test
  void testMusl() {
    executionHelper(
        "linux",
        "amd64",
        "test-linux-musl",
        new MuslDetector() {
          @Override
          public boolean isMusl() {
            return true;
          }
        });
  }

  @Test
  void testMuslArm() {
    executionHelper(
        "linux",
        "aarch64",
        "test-linux-musl-arm",
        new MuslDetector() {
          @Override
          public boolean isMusl() {
            return true;
          }
        });
  }

  @AfterEach
  void cleanup() {
    System.setProperty("os.name", osName);
    System.setProperty("os.arch", osArch);
  }

  private void executionHelper(String os, String arch, String result, MuslDetector muslDetector) {
    System.setProperty("os.name", os);
    System.setProperty("os.arch", arch);
    BinaryExecutionHelper helper = new BinaryExecutionHelper(1, muslDetector);
    assert (helper.executeCommand("test", "test")).equals(BinaryExecutionHelper.ERROR_EXECUTION);
    assert (helper.getExecutionException().getMessage()).contains(result);
  }
}
