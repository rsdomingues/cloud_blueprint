# Building the blueprint step by step
![CI&T Logo](https://us.ciandt.com/themes/custom/ciandt/images/ciandt-logo-for-light.png)

This project is going to take you to step by step to build this pipeline on your own cloud environment. The flow we are implementing is:

![](https://cdn-images-1.medium.com/max/2000/0*TLInRnW04skzzCfv.png)

Let’s get started by creating our GCP environment, we need to create the GKE environment for Development and Production. You can use the provided scripts and strategy to create other environments you may need.

We also gonna configure a Jenkins to manage the production deployment, in this way we are able to apply security policies and be able to keep using the principle of least privilege (PoLP), also known as the principle of minimal privilege used by Google products.

You will need the following tools available on your environment to use all our scripts:

One good option is to use the [Google Cloud Shell](https://cloud.google.com/shell/docs/) that already has the tools installed (apart from Helm).

For the sake of saving time, we created several scripts that can help you, if you want to know everything that we are doing feel free to check them out, they are all in the folder [environment/create](https://github.com/rsdomingues/cloud_blueprint/tree/master/environment/create).

The following is the order that we suggest for the execution.

    #go to the environment 
    cd environment/create 

    #be sure you have configured the environment 
    gcloud info kubectl config view helm version 

    #create the development environment (be sure to activate the GKE api first, in the Google Cloud Console) 
    ./development.sh 

    #create the production environment (helm required) 
    ./production.sh

The Jenkins configuration I am going to break in 2 parts, the first is just the environment creation:

    #go to the environment 
    cd environment 

    #create the jenkins GKE cluster using helm 
    ./create/jenkins.sh 

    #The jenkins environment is not published in a URL by default, to access it use, this comand will give you the URL and the default password for the admin user 
    ./access/jenkins.sh

After creating the environment access the URL given by the script, use the password to gain access to the Jenkins environment. In order to use the deployment job creation script provided, we will need a user token. To create it select Pople.

![](https://cdn-images-1.medium.com/max/2000/0*jU_6Z9uxGeYa_8oc.png)

Then select the admin.

![](https://cdn-images-1.medium.com/max/4048/0*0B-yq7wor3V2641U.png)

An finally, use the Create token option, save the token to use later, it will not be shown to you again, if you lose it, you need to repeat this process (create a new token).

![](https://cdn-images-1.medium.com/max/5200/0*qNyXhFyrwAHEVFkn.png)

Now you can just use the script to create the job

    #go to the environment 
    cd environment/create 

    #run the configuration script 
    ./deploymentjob.sh admin <token_generated_on_jenkins>

The job is created using the [Jenkins Declarative Pipeline](https://jenkins.io/doc/book/pipeline/), and the code for the pipeline is in the [pipelines/prodDeploy/Jenkinsfile](https://github.com/rsdomingues/cloud_blueprint/blob/master/pipelines/prodDeploy/Jenkinsfile) folder in this repository. You can check it out, or create your pipeline using the SCM directly.

If you don’t already know GitHub this quick [guide](https://guides.github.com/activities/hello-world/) will help you create your first repository. If you're not familiar with the Gitflow process, we recommend that you get to know it first.

To enforce the policies we are going to create GitHub offers a feature to secure the branches, we will use this to ensure that every commit to the developer and master branches are checked commits (checked by the CI to ensure coverage, vulnerabilities, code standards, style, etc.). We are also going to use the pull request as a code review feature by requesting at least one approved review for the approval of the pull request.

To configure these options just go to the settings page of your GitHub repository:

![](https://cdn-images-1.medium.com/max/3744/0*DEyoSeRl228GhBJO.png)

Then in the options branch, select the add 2 new rules and configure these options:

* branch name pattern (rule 1: master, rule: 2: develop)

* **Require to pull request reviews before merging**, then configure the number of approvers and if you need the code owner approval. In our case, we require just one approval and it does not need to be the code owner, because that cover more than 80% of the problem and give the development team the independence to define it’s own approach to code review, if its a peer review or the most senior member should execute it.

* **Require status checks to pass before merging**, check the Require branches to be up to date before merging and the continuous-integration/cloudbuild if it is available. This last option will only be available after we complete step 2 and have a CI environment checking the work.

For reference this is our configuration:

![](https://cdn-images-1.medium.com/max/3008/0*bxRGzS-rqsByBr52.png)

With this configuration, whenever a new pull request is created it will only be available to approve if the last commit is a checked one, and the code has at least one review. By default, the admins of the repository have rights to override these rule, but you can configure it to enforce it too. Below is just an example of a pull request page:

![](https://cdn-images-1.medium.com/max/2896/0*qS05skLa-ztdsL8A.png)

The sample application we are going to use is [FooApp](https://github.com/rsdomingues/cloud_blueprint/tree/master/fooapp). Wich is a Java application that echoes anything that does not contain the word bar. The repo has a directory called app where the application code is. To enforce that the code in compliance with our rules, we should create a CI job that on every commit check the unit testing, coverage, and compliance. One option to do so is to use maven running on each developer machine.

In the day to day work, is hard for developers to remember these details, so we can enforce that with a Google Cloud Build trigger for all commits. In that way, the developers can follow their own process and we still can ensure the quality gateways. Also, a good CI environment gives the developer fast feedback for his work. A good practice is to run a build on every commit made to the source code. To achieve that in an on-premise environment is challenging due to the exponential number of builds, but in a cloud environment that is not hard at all. You just have to create a [Google Cloud Trigger](https://cloud.google.com/cloud-build/docs/running-builds/automate-builds) that connects your Github Repository to Google Cloud build.

**Create a trigger**:

Just got to the [Cloud Build](https://console.cloud.google.com/cloud-build/triggers) page at the GCP console and create a new trigger like in this example:

![](https://cdn-images-1.medium.com/max/2188/0*_BL5W1Nvd5KriGfh.png)

Now we can create the cloudbuild.yaml for this CI build, it has 2 steps, one is the build process and if that is ok, let's mark the commit in the Github repository using the Github API. You will find the code below on the /pipelines/onCommit directory of this repository.

    # To build the application from your console use 
    gcloud builds submit --config app/cloudbuild.yaml --substitutions=_GITHUB_TOKEN_ .

As the work progress, the development team will reach a state where the feature is ready for validation from users or shared it with other team members for a broader test. In order to do that, the developer needs to open a pull request to the **develop** branch from his **feature branch**. After that, the team can ensure that the branch is up to date, the last commit of the *feature branch* is in the company standards through code review. With that, the team can approve and merge the code.

Now we need to ensure that the whole component (microservice) is OK, and the business cases that it provide are still up and running with the required performance. To do that we need to run the component and performance testing with the application running.

In order to run the component and performance testing on the application, we need to run the component on a controlled ephemeral environment. With the container power, we can do just that.

Let's create a docker-compose that contains the application and also its dependencies if needed so that we can boot up an environment just for this operation.

**Note**: One important detail is to connect the package we just build using docker to the code version, so if needed we can correctly debug issues. A simple way of achieving that is using them small sha as the tag version of the docker image.

With the test environment ready we can configure another [Google Cloud Build Trigger](https://console.cloud.google.com/cloud-build/triggers) to build execute more complete steps for the commits on the develop branch:

![](https://cdn-images-1.medium.com/max/2208/0*7a4lWk1fPzyQjSzg.png)

Let's create our cloudbuild.yaml file so that it adds the new steps:

* Check the companies patter, just in case.

* Build the docker image

* Boot up our test environment with docker-compose

* Wait for the application to boot (limited resource env)

* Run the component testing using cucumber

* Run the performance testing using gatling

* Submit the images

To deploy to GKE with kubectl, call the kubectl build step to update a Deployment resource:

* Enable the GKE API.

* Add the GKE IAM role:

* In the GCP Console, visit the IAM menu.

* From the list of service accounts, click the Roles drop-down menu beside the Cloud Build [YOUR-PROJECT-NUMBER]@cloudbuild.gserviceaccount.com service account.

* Click Kubernetes Engine, then click Kubernetes Engine Admin.

* Click Save.

In our case, the developer environment is called fooclusterdev, to do the deployment lets just add another step on our development build.

**Notes:**

* One thing to notice is that the version of the image tag, is the commit from the GIT REPOSITORY, with that we are able to always keep track of which code is deployed in each environment.

* The property SHORT_SHA is only set for the SCM trigger build. To trigger it manually just run the following

    gcloud builds submit --config app/cloudbuild.yaml --substitutions=ENV_VAR= .

The last step is to run the deployment to a specific environment. In this repo, you will find scripts to the production deployment, but you can use the same scripts to create as many environments as you need and use the same approach to deploy it.

To execute the deployment go to the Jenkins Console, to access it just run:

    #go to the environment 
    cd environment/access 

    #The jenkins environment is not published in a URL by default, to access it use 
    #the comand will give you the URL and the default password for the admin user 
    ./jenkins.sh

Just access the “DeployToProd” Job and Click Build with Parameters, inform the application version that you want to deploy and then click build, by default, it will use the latest which may not be production ready.

Then wait for it to finish, and you should see the result:

![](https://cdn-images-1.medium.com/max/2376/0*CTffNvPf2YAtxqa3.png)

The deployment uses a blue/green strategy to ensure zero downtime, first it updates a Green environment with the new version, the green deployment do not have access from the production at this point, then it shifts the production traffic to the green deployment, after that the script awaits the “go ahead” from the executor.

![](https://cdn-images-1.medium.com/max/3368/0*k2YcuwpVvIh-OIp_.png)

If the user tests give the “go ahead”, the script updates the blue environment with the new version, which does not have the production traffic and after that it shifts the production traffic back to production.

If the user does not give the “go ahead” the script, just shift the production traffic back to the blue deployment without updating to the new version.

Let’s face it, we have an increasing culture of immediacy that has impacted how people want to engage in all aspects of our lives. Hopefully, with this article you have the ability to reproduce this fully automated environment where both developers and operations have fast feedback, compliance enforced, risk-reduced way of working.

This example was crafted in a way that you can easily adapt parts of it to your specific needs:

* Build tool, from Google Cloud Build to Jenkins, Circle CI, etc

* Deployment orchestration, from Jenkins to Spinnaker, etc

If you have any questions or suggestions please leave a comment here or on Github, and if you have an improvement please send me a pull request.

Project code: [https://github.com/rsdomingues/cloud_blueprint](https://github.com/rsdomingues/cloud_blueprint)
