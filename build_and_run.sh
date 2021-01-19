#!/bin/bash
set -e

# Build and install the libraries
# abstracting away from using the
# RabbitMq message queue
#pushd messaging-utilities
#chmod +x ./build.sh
#./build.sh
#popd

# Build the services

pushd account-service
chmod +x ./build.sh
./build.sh
popd

pushd token-service
chmod +x ./build.sh
./build.sh
popd

pushd dtupay-service
chmod +x ./build.sh
./build.sh
popd

pushd payment-service
chmod +x ./build.sh
./build.sh
popd

# Update the set of services and
# build and execute the system tests
pushd end-to-end-test
chmod +x ./deploy.sh
./deploy.sh 

# just build the services 
chmod +x ./test.sh
./test.sh
popd

# Cleanup the build images
docker image prune -f

