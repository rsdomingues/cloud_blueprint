# Building the blueprint step by step
![CI&T Logo](https://us.ciandt.com/themes/custom/ciandt/images/ciandt-logo-for-light.png)

We are going to take you step by step to build this blueprint on your own cloud environment. The flow we are implementing is:
![Developer Flow](./images/developer_flow.png)

### (Step 0) Creating the GCP Environment

Let's get started by creating our GCP environment, we will create the GKE environment for Development and Production, for the propouse of showing the worflow propoused in this blueprint. You can use the scripts and strategy to create other environments you may need.

We also gona configure an Jenkins to manage the production deployment, in this way we are able to apply security polcies, and be able to keep using the principle of least privilege (PoLP), also known as the principle of minimal privilege used by google products.

You will need the following tools available on your environment to use all our scripts:
- [Google Cloud SDK](https://cloud.google.com/sdk/)
- [Kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)
- [Helm](https://helm.sh/docs/install/#installing-helm)

One good option is to use the [Google Cloud Shell](https://cloud.google.com/shell/docs/) that already has the tools instaled (apart from Helm).

For the sake of save you time, we created several scripts tha can help you, if you want to know every thing that we are doing feel free to check then out, they are all in the [environment/create](https://github.com/rsdomingues/cloud_blueprint/tree/master/environment/create) folder.

The following is the order that we suggest for the execution.

```bash
#go to the environment
cd environment/create

#be sure you have configured the environment
gcloud info
kubectl config view
helm version

#create the development environment (be sure to activate the GKE api first, in the Google Cloud Console)
./development.sh

#create the production environment (helm required)
./production.sh
```

The jenkins we are going to break in 2 parts, the first is just the environment creation:

```bash
#go to the environment
cd environment

#create the jenkins GKE cluster using helm
./create/jenkins.sh

#The jenkins environment is not published in a URL by default, to access it use
#the comand will give you the URL and the default password for the admin user
./access/jenkins.sh
```

After creating the environment access the URL given by the script, use the password to gain access to the jenkins environment. In order to use the `deployment job creation script` provided we will need a user token. To create it select Pople.

![Jenkins Home](images/jenkins_home.png)

Then select the admin.

![Jenkins User](images/jenkins_user.png)

An finally, use the `Create token` option, save the token to use latter, it will not be shown to you again, if you lose it, you need to repeat this process (create a new token).

![Jenkins Token](images/jenkins_token.png)

Now you can jus use the script to create the job

```bash
#go to the environment
cd environment/create

#run the configuration script
./deploymentjob.sh admin <token_generated_on_jenkins>
```
The job is created using the [Jenkins Declarative Pipeline](https://jenkins.io/doc/book/pipeline/), and the code for the pipeline is in the [pipelines/prodDeploy/Jenkinsfile](https://github.com/rsdomingues/cloud_blueprint/blob/master/pipelines/prodDeploy/Jenkinsfile) folter in this repository. You can check it out, or create your pipeline using the SCM directly.

### (Step 1) Creating your source code repository 
---

If you don't already know github this quick [guide](https://guides.github.com/activities/hello-world/) will help you create your first repository. If your not familiar with the Gitflow process, we reccomend that you get to know it first.
 
To enforce the policies we are going to create github offers a feature to secure the branchs, we will use this to ensure that every commit to the developer and master branchs are checked commits (checked by the CI to ensure coverage, vulnarabilities, code standards, style etc.). We are also going to use the pull request as a code review feature by requesting at least one approved review for the approval of the pull request.

To configure this options just go to the settings page of your github repositury:
![Github Repository Settings](./images/github_settings.png)

Then in the options branch, select the add 2 new rules and configure this options:
 - branch name patter (rule 1: `master`, rule: 2: `develop`)
 - **Require pull request reviews before merging**, then configure the amout of aprovers and if you need the code owner aproval. In our case we require just one aproval and it not need to be the code owner, beacause that cover more than 80% of the problem and give the development team the independency to define it's own aproach to code review, if its a peer review or the most senior member should executed it.
 - **Require status checks to pass before merging**, check the `Require branches to be up to date before merging` and the `continuous-integration/cloudbuild` if it is available. This last option will only be available after we complet the step 2 and have a CI environment checking the work.

For reference this is our configuration:
![Branch Configuration](./images/branch_configuration.png)

With this configuration, whenever a new pull request is created it will only be available to aproved if the last commit is a checked one, and the code has at least one review. By default the admins of the repository has rights to override this rules, but you can configure it to enforce it to. Above is just an example of a pull requrest page:

![Pull Request](./images/pull_request.png)

### (Step 2) Unit testing and packaging the app
---
The sample application we are going to use is the [FooApp](https://github.com/rsdomingues/cloud_blueprint/tree/master/fooapp). Wich is a Java application that echoes anything that do not contain the word `bar`. The repo has a directory called app where the application code is. To build ensure that the code is compliance with our rules, we should create an CI job that on every commit check the unit testing, coverage and compliance. To do so we can easely use maven running on each developer machine:

```bash
mvn clean verify
```

In the day to day work, is hard for developers use this details, so we can enforce than with a Google Cloud Build trigger for all commits. In that way the developers can follow their own process and we still can ensure the quality gateways. Also a good CI environment gives the developer a fast feedback for his work. A good practice is to run a build on every commit made to the source code. To achive that in an on-premisse environment is challengin due to the exponential number of builds, but in a cloud environment that is not hard at all. You just have to create an [Google Cloud Trigger](https://cloud.google.com/cloud-build/docs/running-builds/automate-builds) that connects your Github Repository to Google Cloud build.

**Create a trigger**:

Just got to the [Cloud Build](https://console.cloud.google.com/cloud-build/triggers) page at the GCP console and create a new trigger like in this example:
![Cloud Build Trigger](./images/cloud_build_trigger.png)

Now we can create the cloudbuild.yaml for this CI buiild, it has 2 steps, one is the build process and if that is ok, lets mark the commit in the Github repository using the Github API. You will find the code bellow on the /pipelines/onCommit directory of this repository.

```yaml
#Ensure companys policies for the code
steps:

#Ensure tests and coverage on every commit
- name: 'gcr.io/cloud-builders/mvn:3.5.0-jdk-8'
  args: ['verify', 'package', '-q']
  dir: 'fooapp/app'

#Run the github a build check
- name: 'gcr.io/cloud-builders/curl'
  args: ['-u', 'rsdomingues:$_GITHUB_TOKEN', '-X', 'POST', '-d', '{"state": "success","target_url": "https://console.cloud.google.com/cloud-build/builds/$BUILD_ID","description": "The build succeeded!","context": "continuous-integration/cloudbuild"}', 'https://api.github.com/repos/rsdomingues/cloud_blueprint/statuses/$COMMIT_SHA']
  dir:

#For easy filter
tags:
  - "onCommitBuild"
```

```bash
# To build the application from your console use
gcloud builds submit --config app/cloudbuild.yaml --substitutions=_GITHUB_TOKEN="<your git token>" .
```
### (Step 3) Testing performance and business 
---

As the work progress the development team will reach a state where the feature is ready for a validation from users or shared it with other team members for broad test. So he will open a pull reuqest to the **develop** branch from his **feature branch**. And then we will via Github process, ensure that the branch is up to date, the last comit of the **feature brach* is in the company standards and somone else revised the code. With that we can aprove the code review and merge the code.

Now we need to ensure that the whole component (microservice) is OK, and the business cases that it provide are still up and running with the required performance. To do that we need to run the component and performance testing with the application running.

In order to run the component and performance testing on the application, we need to run the component on a controled enviroment, with the container power we can do just that.

Lets create a docker-compose that contais the application and also its dependencies if needed so that we can boot up a environment just for this operation.

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

Just create you docker file using the image not a build and connect it to the cloudbuild network, you also need to give your container a name that will be used for the testing. 

**Note**: One important detail is to conect the package we just build using docker to the code version, so if needed we can correctly debug issues. A simple way of achiving that is using the `small sha` as the tag version of the docker image.


With the test environment ready we can configure another [Google Cloud Build Trigger](https://console.cloud.google.com/cloud-build/triggers) to build execute a more complete steps for the commits on the develop branch:

![Cloud Build Trigger for Develop Branch](images/cloud_build_develop.png)

Lets create our cloudbuild.yaml file so that it adds the new steps:
 - Check the companies patter, just in case.
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
  dir: 'fooapp/app'

#1 create a docker container for the application
- name: 'gcr.io/cloud-builders/docker'
  args: ['build', '--tag=gcr.io/$PROJECT_ID/fooapp:$SHORT_SHA', '.']
  dir: 'fooapp/app'

#2 run the testing environment
- name: 'docker/compose:1.15.0'
  args: ['up', '-d']
  dir: 'fooapp/component-test'
  env:
    - 'PROJECT_ID=$PROJECT_ID'
    - 'SHORT_SHA=$SHORT_SHA'

#3 wait for the application to start
- name: 'gcr.io/cloud-builders/gcloud'
  entrypoint: '/bin/bash'
  args: ['-c', 'sleep 50']
  dir: 'fooapp/component-test'

#4 check if the application has started
- name: 'gcr.io/cloud-builders/gcloud'
  entrypoint: '/bin/bash'
  args: ['-c', './checkApp.sh']
  dir: 'pipelines/devDeploy'

#5 run the business and performance tests
- name: 'gcr.io/cloud-builders/mvn:3.5.0-jdk-8'
  args: ['verify', 'gatling:execute', '-q']
  dir: 'fooapp/component-test'

#6 saves it in the google registry
- name: 'gcr.io/cloud-builders/docker'
  args: ["push", "gcr.io/$PROJECT_ID/fooapp:$SHORT_SHA"]

#For easy filter
tags:
  - "devDeploy"
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

In our case the developer environment is called `fooclusterdev`, to do the deployment lets just add another step on our develop build.

```yaml
[...]

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
  - "devDeploy"
```

**Notes:** 
 - One thing to notice is that the version of image tag, is the commit from the GIT REPOSITORY, with that we are able to always keep track of witch code is deployed in each environment.
 - The propertie `SHORT_SHA` is only set for the SCM trugger build. To trigger it manually just run the following 

```bash
gcloud builds submit --config app/cloudbuild.yaml --substitutions=ENV_VAR="<VALUE>" .
```

### (setp 5 & 6) Deploying the application to other environments including production
---
The last step is to run the deployment to an specific environmento. In this repo you will find scripts to the production deployment, but you can use the same scripts to create as many enviroments as you need and use the same aproach to deploy it.

To execute the deployment go to the Jenkins Console, to access it just run:
```bash
#go to the environment
cd environment/access

#The jenkins environment is not published in a URL by default, to access it use
#the comand will give you the URL and the default password for the admin user
./jenkins.sh
```
Just access the "DeployToProd" Job and Click `Build with Parameters`, inform the application version that you want to deploy and then click `build`, by default it will use the latest witch may not be production ready.

Then wait for it to finish, and you should see the result:
![Jenkins Execution](images/jenkins_execution.png)


The deployment use a blue/green strategy to ensure zero down time, first it update an Green environment with the new version, the green deployment do not have access from the production at this point, then it shift the production traffic to the green deployment, after that the script awaits the "go ahead" from the executor.

![Blue/Green Strategy](images/blue-green.png)

If the user gives the "go ahead", the script update the blue environment with the new version, witch does not have the production traffic and after that it shifts the production traffic back to production.

If the user do not give the "go ahead" the script, just shift the production traffic back to the blue deployment without updating to the new version.


### Conclusion

Let’s face it, we have an increasing culture of immediacy that has impacted how people want to engage in all aspects of our lifes. With this blueprint we have a fully automated enviroment where both developers and operations have a fast feedback, compliance enforced, risk reduced way of working.

This blueprint was created in a way that you can easely adapt parts of it to your specific needs:
 
 - Build tool, from Google Cloud Build to Jenkins, Circle CI etc
 - Deployment orchestration, from Jenkins to Spinnaker etc

Any questions, or if you need any help let us know.