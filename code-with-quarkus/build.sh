#!/bin/bash
set -e
mvn clean package
docker-compose build service1
