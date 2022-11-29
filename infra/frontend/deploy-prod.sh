#!/bin/bash

cd ../../web-app-frontend/

export REACT_APP_ENV=prod
export REACT_APP_BASE_PATH=http://3.75.191.72:80

yarn build

aws s3 sync ./build s3://swozo-project/
