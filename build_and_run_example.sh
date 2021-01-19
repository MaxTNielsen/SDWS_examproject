#!/bin/bash
set -e



# Build the services
pushd token-service
chmod +x ./build.sh
./build.sh
popd 

pushd payment-service
chmod +x ./build.sh
./build.sh
popd 

# Update the set of services and
# build and execute the system tests
pushd demo_client
chmod +x ./deploy.sh
./deploy.sh 
sleep 20s
chmod +x ./test.sh
./test.sh
popd

# Cleanup the build images
docker image prune -f

