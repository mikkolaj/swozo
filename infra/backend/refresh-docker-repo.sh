#!/bin/bash

cd ../..

./gradlew swozo-commons:jar
./gradlew web-app-backend:bootJar

cd web-app-backend

sudo docker build -t swozo-web-app-backend:latest .
sudo docker tag swozo-web-app-backend:latest flok3n/swozo-web-app-backend:latest
sudo docker push flok3n/swozo-web-app-backend:latest
