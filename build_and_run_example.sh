#!/bin/bash
set -e

pushd demo_client
chmod +x ./deployRMQ.sh
./deployRMQ.sh
popd

# Build the services
pushd token-service
chmod +x ./build.sh
./build.sh
popd 

pushd payment-service
chmod +x ./build.sh
./build.sh
popd 

pushd account-manager
chmod +x ./build.sh
./build.sh
popd 

pushd DTUPay
chmod +x ./build.sh
./build.sh
java -jar target/code-with-quarkus-1.0.0-SNAPSHOT-runner.jar &
server_pid=$!
trap 'kill $server_pid' err exit
popd 

# Update the set of services and
# build and execute the system tests
pushd demo_client
chmod +x ./deploy.sh
./deploy.sh 
sleep 3s
chmod +x ./test.sh
./test.sh
popd

# Cleanup the build images
docker image prune -f


$SHELL
