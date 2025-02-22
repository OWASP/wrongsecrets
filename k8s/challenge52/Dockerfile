FROM ubuntu:25.04
RUN useradd -m -u 2000 wrongsecrets

COPY --chown=wrongsecrets executables/ /home/wrongsecrets/
#Note: still needs to call the arm binary when we need to use ARM, so we might need to move this to a shell script as CMD intead.
#use `docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/wrongsecrets-challenge52:4 -f Dockerfile --push .` to push
# use `docker run jeroenwillemsen/wrongsecrets-challenge52:4` to run it
CMD ["./home/wrongsecrets/wrongsecrets-challenge52-c-linux"]
