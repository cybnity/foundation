# Maven build
# Docker launch maven build without need to have maven installed on host
FROM maven:3.8.1-openjdk-11-slim AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean -e -B package

# The officila CYBNITY base image regarding a containerized Java Runtime (based on https://hub.docker.com/_/eclipse-temurin description)
#FROM eclipse-temurin:11-alpine
#FROM eclipse-temurin:11.0.15_10-jre-alpine
#FROM <Linux base image not including Temurin pre-installed>
FROM eclipse-temurin:11-jre-alpine

MAINTAINER Olivier Lemée

ARG PROJECT_ARTIFACT_FINAL_NAME

# Default image's Java home directory is
#ENV JAVA_HOME=/opt/java/openjdk

# Create applications directory
RUN mkdir /opt/app;
WORKDIR /app

# Install the appplications into the container's applications directory

# Copy the Jar file into a safe directory
COPY --from=builder /app/target/${PROJECT_ARTIFACT_FINAL_NAME} /opt/app

# Copy the additional classptha files into the classpath
#COPY --from=builder /app/target/lib/*.*

# See https://www.baeldung.com/executable-jar-with-maven
#<plugin>
#    <groupId>org.apache.maven.plugins</groupId>
#    <artifactId>maven-dependency-plugin</artifactId>
#    <configuration>
#        <outputDirectory>${project.build.directory}/lib</outputDirectory>
#        <excludeTransitive>false</excludeTransitive>
#        <stripVersion>false</stripVersion>
#    </configuration>
#    <executions>
#        <execution>
#            <id>copy-dependencies</id>
#            <phase>package</phase>
#            <goals>
#                <goal>copy-dependencies</goal>
#            </goals>
#        </execution>
#    </executions>
#</plugin>
#<!-- Add LIB folder to classPath -->
#<plugin>
#    <groupId>org.apache.maven.plugins</groupId>
#    <artifactId>maven-jar-plugin</artifactId>
#    <version>2.4</version>
#    <configuration>
#        <archive>
#            <manifest>
#                <addClasspath>true</addClasspath>
#                <classpathPrefix>lib/</classpathPrefix>
#            </manifest>
#        </archive>
#    </configuration>
#</plugin>


# Securize the directories (read-only) hosting the immutable components libraries

COPY ${PROJECT_ARTIFACT_FINAL_NAME} cybnity_module.jar /opt/app

# Start by default the Java application into the JRE instance
CMD ["java", "-jar", "/opt/app/cybnity_module.jar"]
