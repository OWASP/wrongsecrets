FROM ubuntu:plucky-20250619
RUN useradd -m -u 2000 wrongsecrets

COPY --chown=wrongsecrets executables/ /home/wrongsecrets/

#use `docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/wrongsecrets-challenge53:4 -f Dockerfile --push .` to push
# use `docker run jeroenwillemsen/wrongsecrets-challenge53:5` to run it
CMD ["./home/wrongsecrets/start-on-arch.sh"]
