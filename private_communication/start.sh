#!/bin/bash

while true; do
    java -jar /app/private_communication-1.0-SNAPSHOT.jar --spring.profiles.active=online
    echo "Java process exited, restarting..."
    sleep 5
done
