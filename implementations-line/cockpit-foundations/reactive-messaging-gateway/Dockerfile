# Maven build
# Docker launch maven build without need to have maven installed on host
FROM maven:3.8.1-openjdk-11-slim AS builder

# Default configuration path to the Foundation parent maven directory
#ARG MAVEN_PARENT_PROJECT_PATH="../../"

RUN mkdir /project;
WORKDIR /project
ADD . .
# Copy all the Foundation parent project and sub-projects (e.g pom.xml files, /src folders) allowing to build all eventual dependency from local repository
#COPY $MAVEN_PARENT_PROJECT_PATH .
#COPY pom.xml .
#COPY src ./src

# VARIABLE THAT SHOULD BE DEFINED FROM ARGUMENT
# Optional identification of java main artifact (e.g filename of maven artifact generated) to build. Value is usable only during the build of image.
ARG ARTIFACT_RELEASE_VERSION="latest"
ENV BUILT_ARTIFACT_VERSION $ARTIFACT_RELEASE_VERSION
ARG PROFILE_ID="__none__"
ENV PROFILE $PROFILE_ID

# Set the pom version to built
ARG MVN_COMMAND="mvn -B -DgenerateBackupPoms=false -DprocessAllModules=true versions:set -DnewVersion=${BUILT_ARTIFACT_VERSION}"
RUN if [ "$PROFILE_ID" = "__none__" ]; \
    then CMD="$MVN_COMMAND"; \
    else CMD="$MVN_COMMAND -P${PROFILE_ID}"; fi \
    && ${CMD}

# Build maven project including dependencies (-amd option)
ARG MVN_COMMAND="mvn --also-make-dependents --batch-mode --update-snapshots --show-version -Drevision=${BUILT_ARTIFACT_VERSION} --fail-fast --errors --file pom.xml package -Dmaven.test.skip=true"
RUN if [ "$PROFILE_ID" = "__none__" ]; \
    then CMD="$MVN_COMMAND"; \
    else CMD="$MVN_COMMAND -P${PROFILE_ID}"; fi \
    && ${CMD}

# Final built image is based on the official CYBNITY base image regarding a containerized Java Runtime Trusted Execution Environment (TTE)
FROM cybnity/jre-container:latest

MAINTAINER CYBNITY Core Team

# Read (in this new context) the custom defined java component to execute
ARG ARTIFACT_RELEASE_VERSION="latest"
ENV BUILT_ARTIFACT_VERSION $ARTIFACT_RELEASE_VERSION

# Customization of Java application library to execute
# Required identification of java main artifact (e.g filename of maven artifact generated) to start.
# Value is usable during the build of image and during the container runtime.
# Example: myApplication-0.1.0-snapshot.jar
ARG PROJECT_ARTIFACT_FINAL_NAME

# Custom application project sub-folder path
# Example: cockpit-foundations/reactive-messaging-gateway/target
ARG ARTIFACT_PROJECT_TARGET_PATH

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


# Install the jar lib into the container's applications safe directory ($JAVA_APP_DIR_PATH environment variable)
WORKDIR /project
COPY --from=builder ${ARTIFACT_PROJECT_TARGET_PATH}/${PROJECT_ARTIFACT_FINAL_NAME} ${JAVA_APP_DIR_PATH}

# Set the default undefined jar library to start, with the custom copied library name
ENV STARTED_JAR_FILE $PROJECT_ARTIFACT_FINAL_NAME

# Start by default the Java application into the JRE instance
CMD ["java", "-jar", "$JAVA_APP_DIR_PATH/$STARTED_JAR_FILE"]
