FROM adoptopenjdk/openjdk11:alpine as Builder
RUN apk add --no-cache curl tar bash procps

ARG MAVEN_VERSION=3.6.3         
ARG USER_HOME_DIR="/root"
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries

RUN mkdir -p /app
COPY . /app
WORKDIR /app

RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && echo "Downlaoding maven" \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
  \
  && echo "Unziping maven" \
  && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
  \
  && echo "Cleaning and setting links" \
  && rm -f /tmp/apache-maven.tar.gz \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"
RUN cd /app &&  mvn clean package && mv target/web-app-*.jar  target/web-app.jar


FROM adoptopenjdk/openjdk11:alpine
RUN mkdir -p /app
WORKDIR /app
COPY --from=Builder /app/target/web-app.jar /app
EXPOSE 8081

CMD ["java","-jar","/app/web-app.jar","--spring.config.location=/app/application.properties"]
