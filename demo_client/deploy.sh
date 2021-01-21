#!/bin/bash
set -e
# docker image prune -f
# docker-compose build rabbitmq
# docker-compose up -d rabbitmq

echo "rabbitmq compose"

docker-compose build accountmanagementservice
docker-compose up -d accountmanagementservice
docker-compose build tokenservice
docker-compose up -d tokenservice
docker-compose build paymentservice
docker-compose up -d paymentservice
  

