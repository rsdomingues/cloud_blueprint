#!/bin/bash

#create the kubernets cluster for the deployment
echo -e "creating production k8s cluster ..."
gcloud container clusters create fooclusterprd --machine-type n1-standard-2 --num-nodes 1
gcloud container clusters list | grep fooclusterprd

#set production context
export PROD_CONTEXT=$(kubectl config get-contexts | grep fooclusterprd | awk 'END {print $1}')
kubectl config use-context $PROD_CONTEXT

echo -e "configuring the access rules ..."
#Configure security: Add yourself as a cluster administrator in the cluster's RBAC so that you can give Jenkins permissions in the cluster
kubectl create clusterrolebinding cluster-admin-binding --clusterrole=cluster-admin --user=$(gcloud config get-value account)

#Configure security:Grant Tiller, the server side of Helm, the cluster-admin role in your cluster 
kubectl create serviceaccount tiller --namespace kube-system
kubectl create clusterrolebinding tiller-admin-binding --clusterrole=cluster-admin --serviceaccount=kube-system:tiller

echo -e "Initializing helm ..."
#Initialize helm
helm init --service-account=tiller
helm repo update
helm version