# play 実行の Dockerfile
FROM adoptopenjdk/openjdk11

ENV PROJECT_NAME scala_play
ENV VERSION 1.0
ENV SECRET sampleSecret

RUN mkdir /app

#COPY ../target/universal/${PROJECT_NAME}-${VERSION}.zip ./

#RUN unzip ${PROJECT_NAME}-${VERSION}.zip

#WORKDIR /app/${PROJECT_NAME}-${VERSION}

#RUN chmod a+x bin/${PROJECT_NAME}

CMD rm -rf RUNNING_PID && bin/${PROJECT_NAME} -Dapplication.secret=${SECRET}
