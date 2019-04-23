#!/bin/bash
export POD_NAME=$(kubectl get pods -l "component=cd-jenkins-master" -o jsonpath="{.items[0].metadata.name}")
kubectl port-forward $POD_NAME 8080:8080 >> /dev/null &

export JENKINS_PASSWORD=$(printf $(kubectl get secret cd-jenkins -o jsonpath="{.data.jenkins-admin-password}" | base64 --decode);echo)
echo "Use the password: $JENKINS_PASSWORD"
echo "Access it through: http://localhost:8080"