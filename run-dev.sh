cd estimates-service
export ALPHAVANTAGE_PORT=8082
docker-compose down
docker-compose up -d
./mvnw spring-boot:run "-Dspring-boot.run.arguments=--ALPHAVANTAGE_URL=http://localhost --ALPHAVANTAGE_PORT=$ALPHAVANTAGE_PORT --ALPHAVANTAGE_APIKEY=STUBAPIKEY"


