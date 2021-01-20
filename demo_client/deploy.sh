#!/bin/bash
set -e
# docker image prune -f

docker-compose up -d rabbitmq
# sleep 1s

docker-compose up -d accountmanagementservice
docker-compose up -d tokenservice
docker-compose up -d paymentservice
  

