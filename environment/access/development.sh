#!/bin/bash

#set dev context
export DEV_CONTEXT=$(kubectl config get-contexts | grep fooclusterdev | awk 'END {print $2}')
kubectl config use-context $DEV_CONTEXT

#access the applications
export DEV_IP_ADDRESS=$(kubectl get service fooapp-dev |  awk 'END {print $4}')
echo "Access the application: http://$DEV_IP_ADDRESS/swagger-ui.html"