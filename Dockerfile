FROM --platform=$BUILDPLATFORM eclipse-temurin:17-jre

RUN mkdir /app
WORKDIR /app

COPY build/libs/boopster-music.jar /app/boopster.jar

CMD ["java", "-jar", "/app/boopster.jar"]
