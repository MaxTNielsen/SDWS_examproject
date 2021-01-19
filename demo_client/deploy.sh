#!/bin/bash
set -e
docker image prune -f

docker-compose up -d rabbitMq
sleep 10s

docker-compose up -d paymentservice tokenservice accountmanagementservice

