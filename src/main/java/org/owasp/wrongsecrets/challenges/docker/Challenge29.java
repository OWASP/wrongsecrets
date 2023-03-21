package org.owasp.wrongsecrets.challenges.docker;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@Component
@Order(29)
public class Challenge29 extends Challenge {


    public String getMyString() {
        String response = null;
        try {
            // Creating a URL object for the endpoint
            URL url = new URL("http://localhost:8080/getSpecialSecret");

            // Opening an HTTP connection to the endpoint
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Reading the response from the endpoint
            Scanner scanner = new Scanner(connection.getInputStream());
            response = scanner.nextLine();
            scanner.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    public Challenge29(ScoreCard scoreCard) {
        super(scoreCard);
    }

    @Override
    public boolean canRunInCTFMode() {
        return true;
    }


    @Override
    public Spoiler spoiler() {
        return new Spoiler(getMyString());
    }

    @Override
    public boolean answerCorrect(String answer) {
        return getMyString().equals(answer);
    }



    @Override
    public int difficulty() {
        return 2;
    }

    @Override
    public String getTech() {
        return ChallengeTechnology.Tech.FRONTEND.id;
    }

    @Override
    public boolean isLimittedWhenOnlineHosted() {
        return false;
    }


    @Override
    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(RuntimeEnvironment.Environment.DOCKER);
    }





}
