# Stock Investing

## Set up

### Requirements
* Java 17
* Docker Engine 20.10.12
* Docker Compose 1.26.0

### Run
./mvnw spring-boot:run

* root resource is available at http://localhost:8080
* mediatype required to talk to this service is 'application/vnd.stockinvesting.estimates-v1.hal+json'

### Test
./mvnw verify
