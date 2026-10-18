FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -ntp -DskipTests package

FROM tomee:10.2.0-jre17-Temurin-ubuntu-webprofile

RUN rm -rf /usr/local/tomee/webapps/ROOT /usr/local/tomee/webapps/ROOT.war
COPY --from=build /workspace/target/MftOnlineStoreEE.war /usr/local/tomee/webapps/ROOT.war

EXPOSE 8080
