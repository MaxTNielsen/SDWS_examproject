#!/bin/bash
set -e
# docker image prune -f
docker-compose build rabbitmq
docker-compose up -d rabbitmq
  

