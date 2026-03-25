FROM bellsoft/liberica-openjre-debian:25-cds AS builder
WORKDIR /builder

ARG argBasedVersion="1.13.2"

COPY --chown=wrongsecrets target/wrongsecrets-${argBasedVersion}-SNAPSHOT.jar application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

FROM swift:6.0.3-slim AS swift-runtime

FROM eclipse-temurin:25.0.2_10-jre-noble
WORKDIR /application

ARG argBasedPassword="default"
ARG spring_profile=""
ARG challenge59_webhook_url="YUhSMGNITTZMeTlvYjI5cmN5NXpiR0ZqYXk1amIyMHZjMlZ5ZG1salpYTXZWREEwVkRRd1RraFlMMEl3T1VSQlRrb3lUamRMTDJNeWFqYzFSVEUzVjFrd2NFeE5SRXRvU0RsbGQzZzBhdz09"
ENV SPRING_PROFILES_ACTIVE=$spring_profile
ENV ARG_BASED_PASSWORD=$argBasedPassword
ENV APP_VERSION=$argBasedVersion
ENV DOCKER_ENV_PASSWORD="This is it"
ENV AZURE_KEY_VAULT_ENABLED=false
ENV CHALLENGE59_SLACK_WEBHOOK_URL=$challenge59_webhook_url
ENV WRONGSECRETS_MCP_SECRET=MCPStolenSecret42!
ARG GOOGLE_SERVICE_ACCOUNT_KEY="if_you_see_this_configure_the_google_service_account_properly"
ARG GOOGLE_DRIVE_DOCUMENT_ID="1PlZkwEd7GouyY4cdOxBuczm6XumQeuZN31LR2BXRgPs"
ENV GOOGLE_SERVICE_ACCOUNT_KEY=$GOOGLE_SERVICE_ACCOUNT_KEY
ENV GOOGLE_DRIVE_DOCUMENT_ID=$GOOGLE_DRIVE_DOCUMENT_ID
ENV SPRINGDOC_UI=false
ENV SPRINGDOC_DOC=false
ENV BASTIONHOSTPATH="/home/wrongsecrets/.ssh"
ENV PROJECTSPECPATH="/var/helpers/project-specification.mdc"
RUN echo "2vars"
RUN echo "$ARG_BASED_PASSWORD"
RUN echo "$argBasedPassword"

RUN apt-get update && apt-get install -y --no-install-recommends libstdc++6 libicu-dev && rm -rf /var/lib/apt/lists/*

# Copy only the specific Swift runtime libraries required to run the wrongsecrets-swift binary:
# libswiftCore, libswift_Concurrency, libswift_StringProcessing, libswift_RegexParser (direct deps),
# libdispatch (needed by libswift_Concurrency), libBlocksRuntime (needed by libdispatch),
# libswiftGlibc (Swift's POSIX/glibc bindings module, needed by libswift_Concurrency et al.)
# Swift 6.0.3 runtime requires glibc 2.38+ (__isoc23_* symbols). Ubuntu Noble (24.04) has glibc 2.39.
RUN mkdir -p /usr/lib/swift/linux
COPY --from=swift-runtime \
    /usr/lib/swift/linux/libswiftCore.so \
    /usr/lib/swift/linux/libswift_Concurrency.so \
    /usr/lib/swift/linux/libswift_StringProcessing.so \
    /usr/lib/swift/linux/libswift_RegexParser.so \
    /usr/lib/swift/linux/libdispatch.so \
    /usr/lib/swift/linux/libBlocksRuntime.so \
    /usr/lib/swift/linux/libswiftGlibc.so \
    /usr/lib/swift/linux/

# Create the /var/run/secrets2 directory
RUN mkdir -p /var/run/secrets2

# Use a separate RUN command for --mount
RUN --mount=type=secret,id=mysecret \
    export SECRET_VALUE=$(cat /run/secrets/mysecret) && \
    echo $SECRET_VALUE >> /var/run/secrets2/secret.txt

COPY --chown=wrongsecrets .github/scripts/ /var/tmp/helpers
COPY --chown=wrongsecrets .github/scripts/.bash_history /home/wrongsecrets/
COPY --chown=wrongsecrets src/main/resources/executables/wrongsecrets*linux-musl* /home/wrongsecrets/
COPY --chown=wrongsecrets src/main/resources/executables/wrongsecrets-golang-linux /home/wrongsecrets/
COPY --chown=wrongsecrets src/test/resources/alibabacreds.kdbx /var/tmp/helpers
COPY --chown=wrongsecrets src/test/resources/RSAprivatekey.pem /var/tmp/helpers/
COPY --chown=wrongsecrets .ssh/ /home/wrongsecrets/.ssh/
COPY cursor/rules/project-specification.mdc /var/helpers/project-specification.mdc
ENV PROJECT_SPEC_PATH=/var/helpers/project-specification.mdc

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

RUN useradd -u 2000 -m wrongsecrets
USER wrongsecrets

CMD java -jar -XX:SharedArchiveFile=application.jsa -Dspring.profiles.active=$(echo ${SPRING_PROFILES_ACTIVE}) -Dspringdoc.swagger-ui.enabled=${SPRINGDOC_UI} -Dspringdoc.api-docs.enabled=${SPRINGDOC_DOC} -D application.jar
