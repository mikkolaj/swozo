#!/bin/bash

cd ../..

./gradlew swozo-commons:jar
./gradlew orchestrator:bootJar

cd orchestrator

sudo docker build -t swozo-orchestrator:latest .
sudo docker tag swozo-orchestrator:latest flok3n/swozo-orchestrator:latest
sudo docker push flok3n/swozo-orchestrator:latest
