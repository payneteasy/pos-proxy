FROM arm32v7/openjdk:8-jre-slim
ADD https://github.com/payneteasy/pos-proxy/releases/download/1.2-0/server-1.2-SNAPSHOT-jar-with-dependencies.jar /opt/app.jar
WORKDIR /opt
EXPOSE 8081/tcp
CMD ["java", "-jar", "app.jar"]
