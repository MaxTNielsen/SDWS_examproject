# Message Queue Example

The project consists of 4 projects

- A Maven library for some utilities providing an abstraction to accessing the message queue in `libraries/messaging-utilities` which are installled my `mvn install` through the build script
- The payment Microservice in `services/service2` which is build by 
- The money transfer Microservice in `services/service1` 
- The end-to-end tests in `end_to_end_tests`

The main `docker-compose.yml` file is in the `end_to_end_tests`.

To know how the project is build, deployed, and tested, inspect `build_and_run.sh`.