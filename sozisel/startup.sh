#!/bin/bash
cd backend/sozisel
#docker-compose -f docker-compose.yml up --detach
#docker-compose -f jitsi-compose.yml --env-file .jitsi_env up --detach
mix deps.get
mix ecto.setup
mix phx.server
