# Quotes

A Little microservice providing quoted price of stocks of public companies via HATEOAS API

## Set up

### Requirements
* Java 17
* Docker Engine 20.10.12
* Docker Compose 1.26.0
* ports 3306, 8080, 8081 to be free

### 1. Build

./mvnw -f static-analysis/pom.xml clean install

./mvnw install

### 2. Run

#### Usage

* root resource will be available at http://localhost:8081
* API is enabled to self-serve, including live in-service documentation

#### Deploy (dev-env)

docker-compose -f quotes-service/docker-compose.yml up

#### Destroy (dev-env)

docker-compose -f quotes-service/docker-compose.yml down

### 3. Performance Tests (against dev-env)

./mvnw -f quotes-performance/pom.xml gatling:test
