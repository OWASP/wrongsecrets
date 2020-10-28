FROM azul/zulu-openjdk-alpine:15

ARG envPassword
ENV hardcodedEnvPassword="This is it"

ADD target/secrettextprinter-0.0.1-SNAPSHOT.jar /application.jar
RUN java -jar application.jar -DenvPassword=${envPassword}