FROM mysql:8.0.28-debian

COPY src/main/resources/db/migration /docker-entrypoint-initdb.d/
RUN rm -rf /docker-entrypoint-initdb.d/undo

COPY docker /docker/
RUN docker/test/bats/bin/bats docker/test/test.bats
RUN /docker/src/makeScriptsUseQuotesSchema.sh /docker-entrypoint-initdb.d/
RUN rm -rf /docker

RUN echo 'CREATE SCHEMA quotes;' > /docker-entrypoint-initdb.d/V0__create_schema.sql
