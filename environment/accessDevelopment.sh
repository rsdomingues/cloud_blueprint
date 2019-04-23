#!/bin/bash

#access the applications
export DEV_IP_ADDRESS=$(kubectl get service fooapp-dev |  awk 'END {print $4}')
echo "Access the application: http://$DEV_IP_ADDRESS"