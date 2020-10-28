FROM azul/zulu-openjdk-alpine:15

ARG argBasedPassword="default"
ENV ARG_BASED_PASSWORD=$argBasedPassword
ENV DOCKER_ENV_PASSWORD="This is it"

RUN echo "2vars"
RUN echo "$ARG_BASED_PASSWORD"
RUN echo "$argBasedPassword"

ADD target/secrettextprinter-0.0.1-SNAPSHOT.jar /application.jar
ENTRYPOINT ["java","-jar","/application.jar"]