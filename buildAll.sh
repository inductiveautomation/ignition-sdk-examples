#!/usr/bin/env bash

mvn -f AbstractTagDriverExample/pom.xml clean install
mvn -f ComponentExample/pom.xml clean install
mvn -f expression-example/pom.xml clean install
mvn -f gateway-network-example/pom.xml clean install
mvn -f home-connect-example/pom.xml clean install
mvn -f NotificationExample/pom.xml clean install
mvn -f report-component/pom.xml clean install
mvn -f report-datasource-example/pom.xml clean install
mvn -f scripting-rpc-example/pom.xml clean install
mvn -f SimpleTagProviderExample/pom.xml clean install
