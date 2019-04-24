#!/bin/bash

#clone the repository
echo -e "\e[1mGetting jenkins deployment information ..."
git clone https://github.com/GoogleCloudPlatform/continuous-deployment-on-kubernetes.git
cd continuous-deployment-on-kubernetes

#create the kubernets cluster for the deployment
echo -e "\e[1mcreating jenkins k8s cluster ..."
gcloud container clusters create jenkins-cd --machine-type n1-standard-2 --num-nodes 2 --scopes "https://www.googleapis.com/auth/projecthosting,cloud-platform"
gcloud container clusters list | grep jenkins-cd

#set jenkins context
export JENKINS_CONTEXT=$(kubectl config get-contexts | grep jenkins-cd | awk 'END {print $2}')
kubectl config use-context $JENKINS_CONTEXT

echo -e "\e[1mconfiguring the access rules ..."
#Configure security: Add yourself as a cluster administrator in the cluster's RBAC so that you can give Jenkins permissions in the cluster
kubectl create clusterrolebinding cluster-admin-binding --clusterrole=cluster-admin --user=$(gcloud config get-value account)

#Configure security:Grant Tiller, the server side of Helm, the cluster-admin role in your cluster 
kubectl create serviceaccount tiller --namespace kube-system
kubectl create clusterrolebinding tiller-admin-binding --clusterrole=cluster-admin --serviceaccount=kube-system:tiller

echo -e "\e[1mInitializing helm ..."
#Initialize helm
helm init --service-account=tiller
helm repo update
helm version

echo -e "\e[1mdeploing jenkins ..."
#deploy jenkins using helm
helm install -n cd stable/jenkins -f jenkins/values.yaml --version 0.16.6 --wait
kubectl get pods

echo -e "\e[1mall set, enjoy:"
#print the result
kubectl get svc