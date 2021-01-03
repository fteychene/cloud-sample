#!/usr/bin/env bash

function cleanup() {
    rm /tmp/health
}

trap cleanup EXIT

echo "Sample script for application"
echo
echo "Application status"
wget http://localhost:8080/actuator/health -q -O /tmp/health
cat /tmp/health | jq
echo
echo "Environment checkup"
echo "Application id : $APP_ID"
echo "Instance id : $INSTANCE_ID"
echo "Temperature is set to : $TEMPERATURE"
echo "Db is set to : ${DB_ACTIVATED:-false}"


