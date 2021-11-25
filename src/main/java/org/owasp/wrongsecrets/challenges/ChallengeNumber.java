package org.owasp.wrongsecrets.challenges;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ChallengeNumber {

    String value() default "";
}
