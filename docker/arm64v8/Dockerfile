FROM arm64v8/openjdk:8-jre-slim
ADD https://github.com/payneteasy/pos-proxy/releases/download/1.0-1/server-1.0-1-SNAPSHOT-jar-with-dependencies.jar /opt/app.jar
WORKDIR /opt
EXPOSE 8081/tcp
CMD ["java", "-jar", "app.jar"]
