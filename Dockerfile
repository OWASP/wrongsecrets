FROM azul/zulu-openjdk-alpine:15

ARG argBasedPassword="default"
ARG spring_profile=""
ENV SPRING_PROFILES_ACTIVE=$spring_profile
ENV ARG_BASED_PASSWORD=$argBasedPassword
ENV DOCKER_ENV_PASSWORD="This is it"

RUN echo "2vars"
RUN echo "$ARG_BASED_PASSWORD"
RUN echo "$argBasedPassword"

ADD target/secrettextprinter-0.0.2-SNAPSHOT.jar /application.jar
CMD java -jar -Dspring.profiles.active=$(echo ${SPRING_PROFILES_ACTIVE}) application.jar