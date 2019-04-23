#!/bin/bash
#Create the cluster environment
echo "Creating the environment for development"
gcloud container clusters create fooclusterdev --machine-type n1-standard-2 --num-nodes 1

#set dev context
export DEV_CONTEXT=$(kubectl config get-contexts | grep fooclusterdev | awk 'END {print $1}')
kubectl config use-context $DEV_CONTEXT

#create deployment and export the fooapp
echo "deploing application"
export PROJECT_ID=$(gcloud config list --format 'value(core.project)')
kubectl run fooapp-dev --image gcr.io/$PROJECT_ID/fooapp:latest --port 8080
kubectl expose deployment fooapp-dev --type LoadBalancer --port 80 --target-port 8080

echo "all done, to access it run the accessDevelopment.sh"