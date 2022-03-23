# Quotes

A Little microservice providing quoted price of stocks of public companies via HATEOAS API

## Set up

### Requirements
* Java 17
* Docker Engine 20.10.12
* Docker Compose 1.26.0

* port 3306 to be open
* port 8081 to be open
* port 8080 to be open

### 1. Build

./mvnw install

### 2. Run

#### Usage

* root resource will be available at http://localhost:8081
* mediatype required to talk to the service is 'application/vnd.stockinvesting.quotes-v1.hal+json'
* service usage is indicated primarily via HTTP status codes and subsequently via error messages

#### Deploy (dev-env)

docker-compose -f quotes-service/docker-compose.yml up

#### Destroy (dev-env)

docker-compose -f quotes-service/docker-compose.yml down

### 3. Performance Tests (against dev-env)

./mvnw -f quotes-performance/pom.xml gatling:test
