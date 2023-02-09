# The officila CYBNITY base image regarding a containerized Java Runtime (based on https://hub.docker.com/_/eclipse-temurin description)
#FROM eclipse-temurin:11-alpine
#FROM eclipse-temurin:11.0.15_10-jre-alpine
#FROM <Linux base image not including Temurin pre-installed>
FROM eclipse-temurin:11-jre-alpine

MAINTAINER CYBNITY Core Team

# Variable usable during the build and during the runtime phase of this container.
# SHOULD BE REDEFINED BY CUSTOM CONTAINER IMAGE WHICH IMPLEMENT A JAVA APPLICATION CONTAINERIZED
ENV STARTED_JAR_FILE = "MISSING_DEFINED.jar"

# Default image's Java home directory is
#ENV JAVA_HOME=/opt/java/openjdk

# Create application deployment directory
RUN mkdir /opt/cybnity

# Define an environment variate where application components should be stored (already accessible by classpath)
ENV JAVA_APP_DIR_PATH="/opt/cybnity"

# Securize the directories (read-only) hosting the immutable components libraries

# Define the default directory (WORKDIR instruction) when starting the container

# Start by default the Java application into the JRE instance
CMD ["java", "-jar", "$JAVA_APP_DIR_PATH/$STARTED_JAR_FILE"}]
