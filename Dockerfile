# Build container
FROM maven:3.6.3-adoptopenjdk-11 as builder

# Create app directory
WORKDIR /usr/src/app

# Copy source to working directory
COPY . .

RUN mvn -Dmaven.test.skip=true clean package

# Pull build into a second stage deploy container
FROM adoptopenjdk/openjdk11:alpine

LABEL maintainer="Zone24x7 (Private) Limited"

# Copy application data
COPY --from=builder --chown=1001:1001 /usr/src/app/target/web-app-*.jar /usr/src/app/web.jar
COPY --from=builder --chown=1001:1001 /usr/src/app/src/main/resources/application-docker.properties /usr/src/app/application.properties

USER 1001

WORKDIR /usr/src/app

EXPOSE 8081

ENTRYPOINT ["java","-jar","web.jar","--spring.config.location=application.properties"]