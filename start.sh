#!/usr/bin/env sh

export INTERFACE="localhost"
export PORT=8000
export PG_HOST=postgres
export PG_DB=cinema
export PG_USER=postgres
export PG_PASSWORD=admin

docker-compose build
docker-compose up