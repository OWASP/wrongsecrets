package org.owasp.wrongsecrets.challenges.docker;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BinaryExectionHelperTest {


    private String osArch, osName;

    @BeforeEach
    void cacheSystemProperties() {
        osArch = System.getProperty("os.arch");
        osName = System.getProperty("os.name");
    }

    @Test
    void executeOnWindows() {
        if ("windows".equals(osName.toLowerCase())) {
            return; // no need to test this here as you are running on windows ;-).
        }
        executionHelper("Windows", "amd64", "/test-windows.exe");
    }

    @Test
    void executeOnLinuxAmd64() {
        if (osName.toLowerCase().contains("nix") || osName.toLowerCase().contains("lin")) {
            return; // no need to test this here as you are running on linux ;-).
        }
        executionHelper("linux", "amd64", "test-linux");
    }

    @Test
    void executeOnLinuxARM() {
        if (osName.toLowerCase().contains("nix") || osName.toLowerCase().contains("lin")) {
            return; // no need to test this here as you are running on linux ;-).
        }
        executionHelper("linux", "aarch64", "test-linux-arm");
    }

    @Test
    void executeOnMacOS() {
        if (osName.toLowerCase().contains("mac") && osArch.toLowerCase().equals("aarch64")) {
            return; // no need to test this here as you are running on macOS ;-).
        }
        executionHelper("Mac OS X", "aarch64", "test-arm");
    }

    @Test
    void executeOnMacOSARM() {
        if (osName.toLowerCase().contains("mac") && osArch.toLowerCase().equals("x86_64")) {
            return; // no need to test this here as you are running on macOS ;-).
        }
        executionHelper("Mac OS X", "x86_64", "test");
    }

    @Test
    void testMusl() {
        //todo create test!
    }

    @Test
    void testMuslArm() {
        //todo create test!
    }

    @AfterEach
    void cleanup() {
        System.setProperty("os.name", osName);
        System.setProperty("os.arch", osArch);
    }

    private void executionHelper(String os, String arch, String result) {
        System.setProperty("os.name", os);
        System.setProperty("os.arch", arch);
        BinaryExecutionHelper helper = new BinaryExecutionHelper(1);
        assert (helper.executeCommand("test", "test")).equals(BinaryExecutionHelper.ERROR_EXECUTION);
        assert (helper.getExecutionException().getMessage()).contains(result);
    }


}
