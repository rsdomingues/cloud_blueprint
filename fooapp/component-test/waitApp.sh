#!/bin/bash
echo 'Waiting spring-boot to kick start ...' &&
count=1 &&
until $(curl -sSf http://application:8080/actuator/health > /dev/null); do
    echo 'Waiting($count) ...'
    sleep 5
    if [ $count -gt 20 ]
    then
        echo 'Application is not up, abortion operation....'
        exit 1
    fi
    let "count+=1"
done