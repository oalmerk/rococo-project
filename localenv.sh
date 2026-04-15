#!/bin/bash

docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)

docker run --name rococo-all -p 5432:5432 -e POSTGRES_PASSWORD=secret -v pgdata:/var/lib/postgresql/data -v ./postgres/script:/docker-entrypoint-initdb.d -e CREATE_DATABASES=rococo-auth,rococo-museum,rococo-artist,rococo-userdata,rococo-paiting -e TZ=GMT+3 -e PGTZ=GMT+3 -d postgres:15.1 --max_prepared_transactions=100
docker network create kafka-net
docker run -d \
--name zookeeper \
--network kafka-net \
-p 2181:2181 \
-e ZOOKEEPER_CLIENT_PORT=2181 \
confluentinc/cp-zookeeper:7.3.2
docker run -d \
--name kafka \
--network kafka-net \
-p 9092:9092 \
-e KAFKA_BROKER_ID=1 \
-e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
-e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 \
-e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
-e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
-e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
confluentinc/cp-kafka:7.3.2
