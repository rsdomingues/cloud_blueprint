#!/bin/bash

# #set jenkins context
export JENKINS_CONTEXT=$(kubectl config get-contexts | grep jenkins-cd | awk 'END {print $2}')
kubectl config use-context $JENKINS_CONTEXT

# #get info
export POD_NAME=$(kubectl get pods -l "component=cd-jenkins-master" -o jsonpath="{.items[0].metadata.name}")
kubectl port-forward $POD_NAME 8080:8080 >> /dev/null &

# #create the new job
export CRUMB=$(curl -s 'http://localhost:8080/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,":",//crumb)' -u $1:$2)

# create the job full file
cat jobconfig_template_h.xml > jobconfig.xml
cat ../../pipelines/prodDeploy/Jenkinsfile >> jobconfig.xml
cat jobconfig_template_f.xml >> jobconfig.xml

#Create mew application
curl -s -XPOST 'http://localhost:8080/createItem?name=DeployToProd' -u $1:$2 --data-binary @jobconfig.xml -H "$CRUMB" -H "Content-Type:text/xml"
