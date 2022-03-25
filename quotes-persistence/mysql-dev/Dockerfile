FROM org.ignast.stock-investing/testutil.bash.bats:1.0-SNAPSHOT

#Test scrips prepending schema selection to SQL scripts
COPY mysql-dev/bash /code/
RUN bats test/makeScriptsUseQuotesSchemaTest.bats

FROM mysql:8.0.28-debian

#Copy schema migration scripts
COPY src/main/resources/db/migration /docker-entrypoint-initdb.d/
RUN rm -rf /docker-entrypoint-initdb.d/undo

#Prepend schema selection to migration scripts 
COPY mysql-dev/bash/src/makeScriptsUseQuotesSchema.sh /makeScriptsUseQuotesSchema.sh
RUN /makeScriptsUseQuotesSchema.sh /docker-entrypoint-initdb.d/
RUN rm makeScriptsUseQuotesSchema.sh

RUN echo 'CREATE SCHEMA quotes;' > /docker-entrypoint-initdb.d/V0__create_schema.sql
