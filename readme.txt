SpamDetector
=======================

Pre Requisites
---------------
* Eclipse
* Git
* Maven
* Java 7


Steps to Run the application
------------------------------
- Ensure Port 8080 is free before starting the build
- Build Project using : mvn clean install
- WAR file will be created in the target folder : spamdetector-1.0-SNAPSHOT.war

- Build includes the unit tests and Integration tests
- Integration tests are run using an embedded tomcat, which runs on port 8080
- MessageControllerIT contains all the integration tests
