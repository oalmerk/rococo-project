FROM gradle:8.14-jdk21

WORKDIR /home/gradle/rococo
ENV TZ=Europe/Moscow
COPY  ./gradle ./gradle
COPY ./rococo-e-2-e-tests ./rococo-e-2-e-tests
COPY ./rococo-grpc-common ./rococo-grpc-common
COPY ./gradlew ./
COPY ./build.gradle ./
COPY ./settings.gradle ./
#COPY ./gradle.properties ./

CMD gradle test -Dtest.env=docker -Drepository=jpa -Duser.timezone=Europe/Moscow