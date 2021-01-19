#!/bin/bash
set -e



# Build the services
pushd token-service
./build.sh
popd 

pushd service2
./build.sh
popd 

# Update the set of services and
# build and execute the system tests
pushd demo_client
./deploy.sh 
sleep 20s
./test.sh
popd

# Cleanup the build images
docker image prune -f

