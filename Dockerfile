FROM bellsoft/liberica-runtime-container:jdk-23.0.2_9-musl AS builder
WORKDIR /builder
ADD src /builder/wrongsecrets/src
ADD pom.xml /builder/wrongsecrets/pom.xml
ADD mvn* /builder/wrongsecrets/
ADD .mvn /builder/wrongsecrets/.mvn
ADD package* /builder/wrongsecrets/
ADD js /builder/wrongsecrets/js
ENV NODE_PACKAGE_URL  https://unofficial-builds.nodejs.org/download/release/v22.14.0/node-v22.14.0-linux-x64-musl.tar.gz

RUN apk add libstdc++
WORKDIR /opt
RUN wget $NODE_PACKAGE_URL
RUN mkdir -p /opt/nodejs
RUN tar -zxvf *.tar.gz --directory /opt/nodejs --strip-components=1
RUN rm *.tar.gz
RUN ln -s /opt/nodejs/bin/node /usr/local/bin/node
RUN ln -s /opt/nodejs/bin/npm /usr/local/bin/npm

WORKDIR /builder
RUN cd wrongsecrets && ./mvnw -Dmaven.test.skip=true clean compile spring-boot:process-aot package
RUN cd wrongsecrets && zip -d /target/*.jar BOOT-INF/classes/executables/wrongsecrets-golang && \
        zip -d /target/*.jar BOOT-INF/classes/executables/wrongsecrets-golang-arm && \
        zip -d /target/*.jar BOOT-INF/classes/executables/wrongsecrets-dotnet && \
        zip -d /target/*.jar BOOT-INF/classes/executables/wrongsecrets-dotnet-arm && \
        zip -d /target/*.jar BOOT-INF/classes/executables/wrongsecrets-dotnet-linux && \
        zip -d /target/*.jar BOOT-INF/classes/executables/wrongsecrets-dotnet-linux-arm && \
        zip -d /target/*.jar BOOT-INF/classes/executables/*.exe


FROM bellsoft/liberica-runtime-container:jre-23.0.2_9-cds-slim-musl AS optimizer
WORKDIR /optimizer
COPY --from=builder /builder/wrongsecrets/target/*.jar wrongsecrets.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

FROM bellsoft/liberica-runtime-container:jre-23.0.2_9-slim-musl
WORKDIR /application
ARG argBasedVersion="1.11.2A"

ARG argBasedPassword="default"
ARG spring_profile=""
ENV SPRING_PROFILES_ACTIVE=$spring_profile
ENV ARG_BASED_PASSWORD=$argBasedPassword
ENV APP_VERSION=$argBasedVersion
ENV DOCKER_ENV_PASSWORD="This is it"
ENV AZURE_KEY_VAULT_ENABLED=false
ENV SPRINGDOC_UI=false
ENV SPRINGDOC_DOC=false
ENV SPRING_THREADS_VIRTUAL_ENABLED=true

RUN echo "2vars"
RUN echo "$ARG_BASED_PASSWORD"
RUN echo "$argBasedPassword"

# todo: replace or enable (And fix golang) RUN apk add --no-cache libstdc++ icu-libs

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

COPY --from=optimizer /optimizer/extracted/dependencies/ ./
COPY --from=optimizer /optimizer/extracted/spring-boot-loader/ ./
COPY --from=optimizer /optimizer/extracted/snapshot-dependencies/ ./
COPY --from=optimizer /optimizer/extracted/application/ ./


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

CMD java -Dspring.aot.enabled=true -XX:SharedArchiveFile=application.jsa -Dspring.profiles.active=$(echo ${SPRING_PROFILES_ACTIVE}) -Dspringdoc.swagger-ui.enabled=${SPRINGDOC_UI} -Dspringdoc.api-docs.enabled=${SPRINGDOC_DOC} -D -jar application.jar
