#!/usr/bin/env bash

find * -maxdepth 1 -name pom.xml -execdir mvn clean install \;
