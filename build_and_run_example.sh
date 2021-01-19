#!/bin/bash
set -e



# Build the services
pushd service1
./build.sh
popd 

pushd service2
./build.sh
popd 

# Update the set of services and
# build and execute the system tests
pushd end_to_end_tests
./deploy.sh 
sleep 20s
./test.sh
popd

# Cleanup the build images
docker image prune -f

