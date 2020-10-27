FROM openjdk/15-jdk-slim

ARG password

RUN java -jar application.jar $password //TODO create a separate repo, ADD COMPILATION STEP AND COPY IT TO DROPBOX!!