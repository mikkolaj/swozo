#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 -v ORCHESTRATOR_PASSWORD="$ORCHESTRATOR_PASSWORD" --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
      CREATE USER orchestrator WITH PASSWORD :'ORCHESTRATOR_PASSWORD';
      CREATE DATABASE "swozo-orchestrator-db";
      GRANT ALL PRIVILEGES ON DATABASE "swozo-orchestrator-db" TO orchestrator;
EOSQL