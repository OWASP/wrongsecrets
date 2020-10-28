package comcom.example.secrettextprinter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecretLeakageController {

    @Value("${password}")
    String hardcodedPassword;

    @Value("${ARG_BASED_PASSWORD}")
    String argBasedPassword;

    @Value("${DOCKER_ENV_PASSWORD}")
    String hardcodedEnvPassword;


    @GetMapping("/leak-code")
    public String getHardcodedSecret(){
        return hardcodedPassword;
    }

    @GetMapping("/leak-arg")
    public String getEnvArgBasedSecret(){
        return argBasedPassword;
    }

    @GetMapping("/leak-docker-env")
    public String getEnvStaticSecret(){
        return hardcodedEnvPassword;
    }

    @GetMapping("/leak-old")
    public String getOldSecret(){
        return Constants.password;
    }


}
