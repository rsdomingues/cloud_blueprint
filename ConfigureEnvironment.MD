![CI&T Logo](https://us.ciandt.com/themes/custom/ciandt/images/ciandt-logo-for-light.png)

We are going to take you step by step to build this blueprint on your own cloud environment. The flow we are implementing is:
![Developer Flow](./developer_flow.png)


### Creating your source code repository (Step 1)
---

Github and SCM stuff


### Unit testing and packaging the app (Step 2)
---

The sample application we are going to use is the [FooApp](https://github.com/rsdomingues/fooapp). Wich is an Java application that echoes anything that do not contai the word `bar`. The repo has an application called app where the application code is, in the app directory is also located the cloudbuild.yaml file wich is the build descriptor for our CI environment.

```yaml
#Build the application and the package it
steps:

#0 ensure the tests are running before packgint the application
- name: 'gcr.io/cloud-builders/mvn:3.5.0-jdk-8'
  args: ['verify', 'package', '-q']
  dir: 'app'

#saves it in the google registry
images: ['gcr.io/$PROJECT_ID/fooapp']

#For easy filter
tags:
  - "CI"
```

```bash
# To build the application from your console use
gcloud builds submit --config app/cloudbuild.yaml .
```

An good CI environment gives the developer a fast feedback for his work. A good practice is to run a build on every commit made to the source code, in this way you know that your application is always integrated and that you have a precise idea of the state it is. To achive that in an on-premisse environment is challengin due to the exponential number of builds, but in an cloud environment that is not hard at all. You just have to create an [Google Cloud Trigger](https://cloud.google.com/cloud-build/docs/running-builds/automate-builds) that connects your commit to Google Cloud build.

***Note:*** the cloudbuild.yaml file on the [FooApp](https://github.com/rsdomingues/fooapp) contains the fully implemented flow, not partials.

### Testing performance and business (Step 3)
---

Running your component and load testing on the application, to ensure that the componente that was builded is good enought to go to the next phase.

Lets create a docker-compose that contais the application and also its dependencies if needed so that we can boot up a environment just for this operation.

```yaml
version: '2'
services:
  # Dependencies if any


  # FooApp Application
  application:
    #cannot be build for it to work
    image: "gcr.io/${PROJECT_ID}/fooapp:latest"
    #application name is very importante for the test
    container_name: application
    networks:
      - cloudbuild
    ports:
      - "8080:8080"
    environment:
      - "SERVER_PORT:8080"

# Connect to the existing cloudbuild network
networks:
  cloudbuild:
    external: true
```

Just create you docker file using the image not a build and connect it to the cloudbuild network, you also need to give your container a name that will be used for the testing.

Lets update our cloudbuild.yaml file so that it adds the new steps:
 - Build the docker image
 - Boot up our test environment with docker-compose
 - Wait for the application to boot (limited resource env)
 - Run the component testing using cucumber
 - Run the performance testing using gatling
 - Submit the images

```yaml
#Build the application and the package it a docker iamge
steps:

#0 ensure the tests are running before packgint the application
- name: 'gcr.io/cloud-builders/mvn:3.5.0-jdk-8'
  args: ['verify', 'package', '-q']
  dir: 'app'

#2 create a docker container for the application
- name: 'gcr.io/cloud-builders/docker'
  args: ['build', '--tag=gcr.io/$PROJECT_ID/fooapp:latest', '.']
  dir: 'app'

#3 run the testing environment
- name: 'docker/compose:1.15.0'
  args: ['up', '-d']
  dir: 'component-test'
  env:
    - 'PROJECT_ID=$PROJECT_ID'

#4 wait for the application to start
- name: 'gcr.io/cloud-builders/gcloud'
  entrypoint: '/bin/bash'
  args: ['-c', 'sleep 50']
  dir: 'component-test'

#5 check if the application has started
- name: 'gcr.io/cloud-builders/gcloud'
  entrypoint: '/bin/bash'
  args: ['-c', './waitApp.sh']
  dir: 'component-test'

#6 run the business and performance tests
- name: 'gcr.io/cloud-builders/mvn:3.5.0-jdk-8'
  args: ['verify', 'gatling:execute']
  dir: 'component-test'

#saves it in the google registry
images: ['gcr.io/$PROJECT_ID/fooapp']

#For easy filter
tags:
  - "CI"
```

### Automaticly deploy to the developer environment (Step 4)
---

Now that we have a tested version of the application we can deploy it to the developer environment. In order to do that we need to create an environment first, just check the `Creating one environment` at end of this document to boot up yor env.

To deploy to GKE with kubectl, call the kubectl build step to update a Deployment resource:

 - Enable the GKE API.
 - Add GKE IAM role:
   - In GCP Console, visit the IAM menu.
   - From the list of service accounts, click the Roles drop-down menu beside the Cloud Build [YOUR-PROJECT-NUMBER]@cloudbuild.gserviceaccount.com service account.
   - Click Kubernetes Engine, then click Kubernetes Engine Admin.
   - Click Save.

In our case the developer environment is called `fooclusterdev`, to do the deployment lets just add another step on our build.

```yaml
#Build the application and the package it a docker iamge
steps:

#0 ensure the tests are running before packgint the application
- name: 'gcr.io/cloud-builders/mvn:3.5.0-jdk-8'
  args: ['verify', 'package', '-q']
  dir: 'app'

#1 create a docker container for the application
- name: 'gcr.io/cloud-builders/docker'
  args: ['build', '--tag=gcr.io/$PROJECT_ID/fooapp:$SHORT_SHA', '.']
  dir: 'app'

#2 run the testing environment
- name: 'docker/compose:1.15.0'
  args: ['up', '-d']
  dir: 'component-test'
  env:
    - 'PROJECT_ID=$PROJECT_ID'
    - 'SHORT_SHA=$SHORT_SHA'

#3 wait for the application to start
- name: 'gcr.io/cloud-builders/gcloud'
  entrypoint: '/bin/bash'
  args: ['-c', 'sleep 50']
  dir: 'component-test'

#4 check if the application has started
- name: 'gcr.io/cloud-builders/gcloud'
  entrypoint: '/bin/bash'
  args: ['-c', './waitApp.sh']
  dir: 'component-test'

#5 run the business and performance tests
- name: 'gcr.io/cloud-builders/mvn:3.5.0-jdk-8'
  args: ['verify', 'gatling:execute', '-q']
  dir: 'component-test'

#6 saves it in the google registry
- name: 'gcr.io/cloud-builders/docker'
  args: ["push", "gcr.io/$PROJECT_ID/fooapp:$SHORT_SHA"]

#7 deploy the application to the environment
- name: 'gcr.io/cloud-builders/kubectl'
  args:
    - 'set'
    - 'image'
    - 'deployment'
    - 'fooapp-dev'
    - 'fooapp-dev=gcr.io/$PROJECT_ID/fooapp:$SHORT_SHA'
  env:
    - 'CLOUDSDK_COMPUTE_ZONE=us-west1-a'
    - 'CLOUDSDK_CONTAINER_CLUSTER=fooclusterdev'

#For easy filter
tags:
  - "CI"
```

**Note:** one thing to notice is that the version of image tag, is the commit from the GIT REPOSITORY, with that we are able to always keep track of witch code is deployed in each environment. And ther is also need to change the docker-compose.yaml

```yaml
version: '2'
services:
  # Dependencies if any


  # FooApp Application
  application:
    #cannot be build for it to work
    image: "gcr.io/${PROJECT_ID}/fooapp:${SHORT_SHA}"
    #application name is very importante for the test
    container_name: application
    networks:
      - cloudbuild
    ports:
      - "8080:8080"
    environment:
      - "SERVER_PORT:8080"

# Connect to the existing cloudbuild network
networks:
  cloudbuild:
    external: true
```

The propertie `SHORT_SHA` is only set for the SCM trugger build. To trigger it manually just run the following 

```bash
gcloud builds submit --config app/cloudbuild.yaml --substitutions=SHORT_SHA="[SHORT_SHA_REPLACEMENT]" .
```

### Deploying the application to other environments (setp 5)
---

### Deploying the application to Production environment (setp 6)
---

##Creating one environment
Let's start by creating our K8S cluster called `foocluster` on GKE. On you Google Cloud Shell:

```bash
# Set your current prefered COMPUTE_ZONE for instance us-west1-a
gcloud config set compute/zone [COMPUTE_ZONE]

# Create the foocluster with defalt parameters
gcloud container clusters create foocluster
```

***Testing the environment (optional)***

Creating the Deployment
To run hello-app in your cluster, run the following command:

```bash
kubectl run hello-server --image gcr.io/google-samples/hello-app:1.0 --port 8080
```
This Kubernetes command, kubectl run, creates a new Deployment named hello-server. The Deployment's Pod runs the hello-app image in its container.

In this command:

- `image` specifies a container image to deploy. In this case, the command pulls the example image from a Google Container Registry bucket, gcr.io/google-samples/hello-app. :1.0 indicates the specific image version to pull. If a version is not specified, the latest version is used.
- `port` specifies the port that the container exposes.

To expose your application, run the following kubectl expose command:

```bash
kubectl expose deployment hello-server --type LoadBalancer \
  --port 80 --target-port 8080
```

Inspect the hello-server Service by running kubectl get:
```bash
kubectl get service hello-server

#Access the service by using http://[EXTERNAL_IP]/
```

**Clean up**
To avoid incurring charges to your GCP account for the resources used in this sample:

Delete the application's Service by running kubectl delete:

```bash
#This will delete the Compute Engine load balancer that you created when you exposed the deployment.
kubectl delete service hello-server


#Delete your cluster by running gcloud container clusters delete:
gcloud container clusters delete foocluster
```