FROM ubuntu:25.04
RUN useradd -m -u 2000 wrongsecrets

COPY --chown=wrongsecrets executables/ /home/wrongsecrets/
CMD ["/home/wrongsecrets/wrongsecrets-challenge52-c &"]
