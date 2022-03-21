# Quotes

A Little microservice providing quoted price of stocks of public companies via HATEOAS API

## Set up

### Requirements
* Java 17
* Docker Engine 20.10.12
* Docker Compose 1.26.0

* port 3306 open
* port 8081 open
* port 8080 open

### 1. Build

./mvnw install

### 2. Run

#### Usage

* root resource will be available at http://localhost:8080
* mediatype required to talk to this service is 'application/vnd.stockinvesting.quotes-v1.hal+json'

#### Deploy (dev-env)

./run.sh

#### Destroy (dev-env)

./destroy.sh

### 3. Performance Tests (against dev-env)

#### Setup

./mvnw -f quotes-performance/pom.xml gatling:test -Dgatling.simulationClass=org.ignast.stockinvesting.quotes.performance.simulation.Setup

#### Execute

./mvnw -f quotes-performance/pom.xml gatling:test -Dgatling.simulationClass=org.ignast.stockinvesting.quotes.performance.simulation.PriceSimulation
