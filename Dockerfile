FROM eclipse-temurin:17_35-jdk-focal

ARG argBasedPassword="default"
ARG argBasedVersion="0.0.0"
ARG spring_profile=""
ENV SPRING_PROFILES_ACTIVE=$spring_profile
ENV ARG_BASED_PASSWORD=$argBasedPassword
ENV APP_VERSION=$argBasedVersion
ENV DOCKER_ENV_PASSWORD="This is it"

RUN echo "2vars"
RUN echo "$ARG_BASED_PASSWORD"
RUN echo "$argBasedPassword"

RUN useradd -ms /bin/bash wrongsecrets
RUN chgrp -R 0 /home/wrongsecrets
RUN chmod -R g=u /home/wrongsecrets
COPY --chown=wrongsecrets target/wrongsecrets-0.0.2-SNAPSHOT.jar /home/wrongsecrets/application.jar
COPY --chown=wrongsecrets .github/scripts/ /var/tmp/helpers
WORKDIR /home/wrongsecrets
CMD java -jar -Dspring.profiles.active=$(echo ${SPRING_PROFILES_ACTIVE})  /home/wrongsecrets/application.jar
