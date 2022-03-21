#!/bin/bash

cd quotes-service
docker-compose down
docker-compose up -d
../mvnw -f pom.xml spring-boot:run


