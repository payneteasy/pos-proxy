FROM openjdk:8-jre-slim
ADD https://github.com/payneteasy/pos-proxy/releases/download/1.3.1/pos-proxy-1.3.1.jar /opt/app.jar
WORKDIR /opt
EXPOSE 8081/tcp
CMD ["java", "-jar", "app.jar"]
