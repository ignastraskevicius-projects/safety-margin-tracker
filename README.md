# Stock Investing

## Set up

### Requirements
* Java 17
* Docker Engine 20.10.12
* Docker Compose 1.26.0

### 1. Build

./mvnw install

### 2. Run

* root resource is available at http://localhost:8080
* mediatype required to talk to this service is 'application/vnd.stockinvesting.estimates-v1.hal+json'

#### Production setup (against real alphavantage service)

./run-prod.sh

#### Development setup (against alphavantage simulator)

./run-dev.sh
