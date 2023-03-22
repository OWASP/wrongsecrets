package org.owasp.wrongsecrets.challenges.docker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.ScoreCard;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
@ExtendWith(MockitoExtension.class)
class Challenge29Test {

    @Mock
    private ScoreCard scoreCard;
    //
    @Test
    void testGetMyString() {
        Challenge29 challenge = new Challenge29(scoreCard);
        String response = challenge.getMyString();

        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    void testGetSpecialSecretEndpoint() throws Exception {
        URL url = new URL("http://localhost:8080/getSpecialSecret");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode);

        Scanner scanner = new Scanner(connection.getInputStream());
        String response = scanner.nextLine();
        scanner.close();

        assertNotNull(response);
        assertFalse(response.isEmpty());
    }
}
