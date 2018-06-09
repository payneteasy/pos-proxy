# pos-proxy

[![Build Status](https://travis-ci.org/payneteasy/pos-proxy.svg?branch=master)](https://travis-ci.org/payneteasy/pos-proxy)
[![CircleCI](https://circleci.com/gh/payneteasy/pos-proxy.svg?style=svg)](https://circleci.com/gh/payneteasy/pos-proxy)

## How to build and run

```
./mvnw clean package
cd server/target && java -jar server-1.0-1-SNAPSHOT-jar-with-dependencies.jar
```

## How to run from release page

```
wget https://github.com/payneteasy/pos-proxy/releases/download/1.0-1/server-1.0-1-SNAPSHOT-jar-with-dependencies.jar
java -jar server-1.0-1-SNAPSHOT-jar-with-dependencies.jar
```

## Swagger UI

* Run the application
* Go to the http://0.0.0.0:8081/pos-proxy/swagger-ui

## How to generate a client code

* Install swagger-codegen from https://github.com/swagger-api/swagger-codegen
* Run swagger-codegen with your language
```
swagger-codegen generate -i http://0.0.0.0:8081/pos-proxy/swagger-ui/pos-proxy.json -l java
```

## Sequence diagram

![Sequence diagram](https://raw.githubusercontent.com/payneteasy/pos-proxy/master/doc/diagram.png)

Where:
* Client - a client code to start a payment
* POS Proxy - this application
* gate.payneteasy.com - Our authorisation server
* Merchant site - we can make a callback to your server with a final order status
