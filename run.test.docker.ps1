mvn clean install -pl ui.tests -Pui-tests-docker-build
mvn verify -pl ui.tests  -Pui-tests-docker-execution -DAEM_AUTHOR_URL=http://host.docker.internal:4502


