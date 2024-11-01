FROM eclipse-temurin:23.0.1_11-jre-alpine

ARG argBasedPassword="default"
ARG argBasedVersion="1.8.5"
ARG spring_profile=""
ENV SPRING_PROFILES_ACTIVE=$spring_profile
ENV ARG_BASED_PASSWORD=$argBasedPassword
ENV APP_VERSION=$argBasedVersion
ENV DOCKER_ENV_PASSWORD="This is it"
ENV AZURE_KEY_VAULT_ENABLED=false
ENV SPRINGDOC_UI=false
ENV SPRINGDOC_DOC=false

RUN echo "2vars"
RUN echo "$ARG_BASED_PASSWORD"
RUN echo "$argBasedPassword"

RUN apk add --no-cache libstdc++ icu-libs

#RUN useradd -u 2000 -m wrongsecrets
RUN adduser -u 2000 -D wrongsecrets
USER wrongsecrets

COPY --chown=wrongsecrets target/wrongsecrets-${argBasedVersion}-SNAPSHOT.jar /application.jar
COPY --chown=wrongsecrets .github/scripts/ /var/tmp/helpers
COPY --chown=wrongsecrets .github/scripts/.bash_history /home/wrongsecrets/
COPY --chown=wrongsecrets src/main/resources/executables/*linux-musl* /home/wrongsecrets/
COPY --chown=wrongsecrets src/test/resources/alibabacreds.kdbx /var/tmp/helpers
COPY --chown=wrongsecrets src/test/resources/RSAprivatekey.pem /var/tmp/helpers/
USER wrongsecrets
CMD java -jar -Dspring.profiles.active=$(echo ${SPRING_PROFILES_ACTIVE}) -Dspringdoc.swagger-ui.enabled=${SPRINGDOC_UI} -Dspringdoc.api-docs.enabled=${SPRINGDOC_DOC} -D /application.jar
