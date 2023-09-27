FROM gradle:8-jdk17-alpine

# RUN apt-get update \
#     && apt-get install -y figlet \
#     && rm -rf /var/lib/apt/lists/*



WORKDIR /aplicativo

COPY ./appjava /aplicativo

RUN gradle installDist

CMD /aplicativo/app/build/install/app/bin/app
