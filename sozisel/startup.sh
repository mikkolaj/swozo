#!/bin/bash
cd backend
docker-compose -f docker-compose.yml up --detach
docker-compose -f jitsi-compose.yml --env-file .jitsi_env up --detach
cd sozisel
mix deps.get
apk add erlang-dev
mix local.rebar --force
mix ecto.setup
mix phx.server