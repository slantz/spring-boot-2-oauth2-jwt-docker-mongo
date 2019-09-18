FROM openjdk:10.0.1-10-jdk-slim-sid

RUN apt-get update && apt-get install -y --no-install-recommends curl \
	&& rm -rf /var/lib/apt/lists/*

ARG JAR_FILE

COPY ${JAR_FILE} /opt/workspace/app.jar

WORKDIR /opt/workspace

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "--add-modules", "java.xml.bind", "-jar", "app.jar"]

HEALTHCHECK --start-period=10s CMD curl -f http://localhost:8081/actuator/health || exit 1