# Quarkus Coffeeshop Core Microservice

This project uses Quarkus, the Supersonic Subatomic Java Framework.  If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Requirements

*Docker*
You need Docker Desktop to run the tests because the application uses Testcontainers, which use Docker, for testing

*Docker Compose*
It is easy to develop locally using Docker Compose to spin up the services the application depends on.  You can of course install MongoDB and Kafka locally, but the instructions will refer to Docker

## Working Locally

All services in the application can be run locally

### Environment variables

Quarkus' configuration can be environment specific: https://quarkus.io/guides/config

This service uses the following environment variables when running with the production profile:
* MONGO_DB
* MONGO_URL
* MONGO_USERNAME
* MONGO_PASSWORD
* KAFKA_BOOTSTRAP_URLS

You can set them all at once with the following line:
```shell
export MONGO_DB=cafedb MONGO_URL=mongodb://cafe-user:redhat-20@localhost:27017/cafedb MONGO_USERNAME=cafe-user MONGO_PASSWORD=redhat-20 KAFKA_BOOTSTRAP_URLS=localhost:9092
```

And start Quarkus in dev mode with:

```java
./mvnw clean compile quarkus:dev -Ddebug=5006
```

### Kafka Consumers and Producers

If you have Kafka's command line tools installed you can watch the topics with:

```shell script
kafka-console-consumer --bootstrap-server localhost:9092 --topic orders --from-beginning
kafka-console-producer --broker-list localhost:9092 --topic orders
```

## OpenShift Deployment 
**Deploy quarkus-cafe-core on OpenShift**
```
$ oc login https://api.ocp4.examaple.com:64443
$ oc project quarkus-cafe-demo
$ oc new-app quay.io/quarkus/ubi-quarkus-native-s2i:20.0.0-java11~https://github.com/jeremyrdavis/quarkus-cafe-demo.git --context-dir=quarkus-cafe-core --name=quarkus-cafe-core
```

**To delete quarkus-cafe-core application**
```
$ oc delete all --selector app=quarkus-cafe-core
```

## Tests

logback.xml is included for the Testcontainers logging

## Packaging and running the application

```shell
./mvnw clean package -Pnative -Dquarkus.native.container-build=true
docker build -f src/main/docker/Dockerfile.native -t <<DOCKER_HUB_ID>>/quarkuscoffeeshop-counter .
export MONGO_DB=cafedb MONGO_URL=mongodb://cafe-user:redhat-20@localhost:27017/cafedb MONGO_USERNAME=cafe-user MONGO_PASSWORD=redhat-20 KAFKA_BOOTSTRAP_URLS=localhost:9092
docker run -i --network="host" -e MONGO_DB=${MONGO_DB} -e MONGO_URL=${MONGO_URL} -e MONGO_USERNAME=${MONGO_USERNAME} -e MONGO_PASSWORD=${MONGO_PASSWORD} -e KAFKA_BOOTSTRAP_URLS=${KAFKA_BOOTSTRAP_URLS} <<DOCKER_HUB_ID>>/quarkuscoffeeshop-counter:latest
docker images -a | grep counter
docker tag <<RESULT>> <<DOCKER_HUB_ID>>/quarkuscoffeeshop-counter:<<VERSION>>
```
## CHANGELOG

3.1.0
* refactored package "io.quarkuscoffeeshop.core" to "io.quarkuscoffeeshop.counter"
* Added persistence for events in orders topic