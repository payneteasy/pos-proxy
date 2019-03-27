# pos-proxy

[![Build Status](https://travis-ci.org/payneteasy/pos-proxy.svg?branch=master)](https://travis-ci.org/payneteasy/pos-proxy)
[![CircleCI](https://circleci.com/gh/payneteasy/pos-proxy.svg?style=svg)](https://circleci.com/gh/payneteasy/pos-proxy)

## How it works

POS Proxy is a web server that redirects json requests to a POS terminal connected via USB.
You can also use this example to see how to integrate the Payneteasy mPOS SDK to your java application.

![Sequence diagram](https://raw.githubusercontent.com/payneteasy/pos-proxy/master/doc/diagram.png)

* Client - a client code to start a payment
* POS Proxy - this application
* gate.payneteasy.com - Our authorisation server
* Merchant site - we can make a callback to your server with a final order status

## Swagger UI

* Run the application
* Go to the http://0.0.0.0:8081/pos-proxy/swagger-ui

Please see the video below:

[![Swagger UI](https://img.youtube.com/vi/_A6wEbFHIOI/0.jpg)](https://youtu.be/_A6wEbFHIOI)

## How to generate a client code

* Install swagger-codegen from https://github.com/swagger-api/swagger-codegen
* Run swagger-codegen with your language

```bash
swagger-codegen generate -i http://0.0.0.0:8081/pos-proxy/swagger-ui/pos-proxy.json -l java
```

## Run it from docker

### amd64

```bash
docker run -it -p 8081:8081/tcp  payneteasy/pos-proxy:amd64
```

### arm 64 v8 (Raspberry PI 3)

```bash
docker run -it -p 8081:8081/tcp  payneteasy/pos-proxy:arm64v8
```

### arm 32 v7 (Raspberry PI 1/2)

```bash
docker run -it -p 8081:8081/tcp  payneteasy/pos-proxy:arm32v7
```

## Requirements

* Java 1.8 (Oracle or OpenJDK)

## How to build and run

```bash
./mvnw clean package
cd server/target && java -jar server-1.0-1-SNAPSHOT-jar-with-dependencies.jar
```

## How to run from release page

```bash
wget https://github.com/payneteasy/pos-proxy/releases/download/1.0-1/server-1.0-1-SNAPSHOT-jar-with-dependencies.jar
java -jar server-1.0-1-SNAPSHOT-jar-with-dependencies.jar
```

## How to change a listening port

You can change the listening port by pass this via -D option or via environment variable.

Via environment variable:
```bash
export HTTP_SERVER_PORT=9090
java -jar server-1.0-1-SNAPSHOT-jar-with-dependencies.jar
```

Via -D option:
```bash
java -DHTTP_SERVER_PORT=9090 -jar server-1.0-1-SNAPSHOT-jar-with-dependencies.jar
```
