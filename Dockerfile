FROM bellsoft/liberica-openjre-debian:23.0.1-13-cds AS builder
WORKDIR /builder

ARG argBasedVersion="1.10.3"

COPY --chown=wrongsecrets target/wrongsecrets-${argBasedVersion}-SNAPSHOT.jar application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

FROM eclipse-temurin:23.0.1_11-jre-alpine
WORKDIR /application

ARG argBasedPassword="default"
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

# Create the /var/run/secrets2 directory
RUN mkdir -p /var/run/secrets2

# Use a separate RUN command for --mount
RUN --mount=type=secret,id=mysecret \
    export SECRET_VALUE=$(cat /run/secrets/mysecret) && \
    echo $SECRET_VALUE >> /var/run/secrets2/secret.txt

COPY --chown=wrongsecrets .github/scripts/ /var/tmp/helpers
COPY --chown=wrongsecrets .github/scripts/.bash_history /home/wrongsecrets/
COPY --chown=wrongsecrets src/main/resources/executables/*linux-musl* /home/wrongsecrets/
COPY --chown=wrongsecrets src/test/resources/alibabacreds.kdbx /var/tmp/helpers
COPY --chown=wrongsecrets src/test/resources/RSAprivatekey.pem /var/tmp/helpers/

COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./


# Mock the service account token for CDS profile generation
RUN mkdir -p /var/run/secrets/kubernetes.io/serviceaccount && \
    echo "mock-token" > /var/run/secrets/kubernetes.io/serviceaccount/token && \
    chmod 600 /var/run/secrets/kubernetes.io/serviceaccount/token

# Create a dynamic archive
RUN java -XX:ArchiveClassesAtExit=application.jsa -Dspring.context.exit=onRefresh -jar application.jar

# Clean up the mocked token
RUN rm -rf /var/run/secrets/kubernetes.io

# Static archive
# RUN java -Xshare:off -XX:DumpLoadedClassList=application.classlist -Dspring.context.exit=onRefresh -jar application.jar
# RUN java -Xshare:dump -XX:SharedArchiveFile=application.jsa -XX:SharedClassListFile=application.classlist -Dspring.context.exit=onRefresh -cp application.jar

RUN adduser -u 2000 -D wrongsecrets
USER wrongsecrets

CMD java -jar -XX:SharedArchiveFile=application.jsa -Dspring.profiles.active=$(echo ${SPRING_PROFILES_ACTIVE}) -Dspringdoc.swagger-ui.enabled=${SPRINGDOC_UI} -Dspringdoc.api-docs.enabled=${SPRINGDOC_DOC} -D application.jar
