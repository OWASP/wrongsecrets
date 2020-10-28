package comcom.example.secrettextprinter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticateController {

    //todo: rewrite as funny onepassword auth (posts!)
    @Value("${password}")
    String password;

    @Value("${envPassword}")
    String envBasedPassword;

    @Value("${hardcodedEnvPassword}")
    String hardcodedEnvPassword;

    @GetMapping("/leakit")
    public String getSecret(){
        return password;
    }

    @GetMapping("/leakit-env")
    public String getEnvSecret(){
        return envBasedPassword;
    }

    @GetMapping("/leakit-docker-env")
    public String getEnvStaticSecret(){
        return hardcodedEnvPassword;
    }

    @GetMapping("/leakold")
    public String getOldSecret(){
        return Constants.password;
    }


}
