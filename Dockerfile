FROM eclipse-temurin:18_36-jdk-focal

ARG argBasedPassword="default"
ARG argBasedVersion="0.0.0"
ARG spring_profile=""
ENV SPRING_PROFILES_ACTIVE=$spring_profile
ENV ARG_BASED_PASSWORD=$argBasedPassword
ENV APP_VERSION=$argBasedVersion
ENV DOCKER_ENV_PASSWORD="This is it"
ENV AZURE_KEY_VAULT_ENABLED=false

RUN echo "2vars"
RUN echo "$ARG_BASED_PASSWORD"
RUN echo "$argBasedPassword"

RUN useradd -u 2000 -m wrongsecrets

COPY --chown=wrongsecrets target/wrongsecrets-${argBasedVersion}-SNAPSHOT.jar /application.jar
COPY --chown=wrongsecrets .github/scripts/ /var/tmp/helpers
COPY --chown=wrongsecrets src/main/resources/.bash_history /home/wrongsecrets/
COPY --chown=wrongsecrets src/test/resources/alibabacreds.kdbx /var/tmp/helpers
USER wrongsecrets
CMD java -jar -Dspring.profiles.active=$(echo ${SPRING_PROFILES_ACTIVE}) /application.jar
