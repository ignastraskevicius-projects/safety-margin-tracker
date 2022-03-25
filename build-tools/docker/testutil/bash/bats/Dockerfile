FROM bats/bats:1.6.0

RUN apk update
RUN apk add git

RUN git clone https://github.com/bats-core/bats-support.git /opt/test_helper/bats-support
RUN git clone https://github.com/bats-core/bats-file.git /opt/test_helper/bats-file
RUN git clone https://github.com/bats-core/bats-assert.git /opt/test_helper/bats-assert

#Ensuring Bats libs are setup correctly
COPY docker/testutil/bash/bats/test/testBatsSetup.bats /code/
RUN bats testBatsSetup.bats
RUN rm /code/testBatsSetup.bats
