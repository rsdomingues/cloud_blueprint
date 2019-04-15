![CI&T Logo](https://us.ciandt.com/themes/custom/ciandt/images/ciandt-logo-for-light.png)

We are a global digital technology solutions partner, a pioneer in design thinking, lean digital transformation, and advanced technologies. For over 20 years, CI&T has been a trusted partner of industry leading brands. This bluprint was created by our spetialist team to help you on your digital solutions path.

If you like it, or want to know more, contact us: https://us.ciandt.com/

# Github Google Cloud Blueprint for FinServ
An all-purpose application blueprint to kickstart a successful DevOps workflow on Google Cloud Plataform. Using a lot of open software tools.

This repository contains instructions for people willing to utilize our GCP  blueprint to kickstart the creation of bespoke solutions based on GitHub, GCP,Docker with GKE as well, being connected to a Jenkins for a CI/CD pipeline to illustrate a blue/green strategy for application architectures.

# Business Value Proposition

The “microservices” blueprint helps you visualize what a modern development workflow looks like and how it could be implemented in organizations at scale,using a baseline definition that can be expanded as needed depending on specific requirements.

# Blueprint Description

The goal of this GCP blueprint is to illustrate how teams can collaborate efficiently on different application repositories on GitHub and go from pull request to production with guaranteed zero downtime thanks to a blue/green deployment strategy.

It is designed to allows collaborators to leverage an agile workflow while tracking all work items for regulatory purposes. The tight integration offered by the GKE solution simplifies the deployment and operations of a Kubernetes based “service mesh” and enables teams to dynamically scale the application infrastructure with confidence and agility.

The following diagram shows the main responsability of each tool, we will explore in more details each phase and step of the process.

![Strategic Workflow](./strategic_workflow.png)

## Techinical details
---
Now let's explore each step of the process in details:

**The application** 

For this DevOps workflow we need an sample app, for this we are going to use the [FooApp](https://github.com/rsdomingues/fooapp), wich is an simple cloud native java application build on top of the [Spring Cloud Framework](https://spring.io/projects/spring-cloud). Feel free to explore it, because it has some example of automated testing, clean architecture and some other stuff.

**Development process**
To give our developer the ability to focus on the application managment part we are going to build a fully automated pipeline that will take our code to produciton. For the right control we are going to insert control gates on the pipeline for our binaries, so that we can ensure the builded an tested aplication is deployed sucessully and risk free to the production environment. Focusing on the binaries also alow us to provide our developers with an choice to how manage their source code. The more detail steps of the flow will be as follows.

![Developer Flow](./developer_flow.png)

A little more details

**Source Control Managment**

One big importante element to the teams productivity is the SCM process, to enable fast and secure development. Nowadays there are two main aproachs used by most companies Gitflow and Trunk Based Development (TDB). While TDB provide a much faster aproach, for most teams it is dificult do adopt it, a much easyer aproach is to adopt the Gitflow. So for our example we gona use Gitflow.

**Project Managment**

For project managment we are going to use Jira, so that we can control the issues and tickers for the entire development process not only for development issues.

**Packaging and Deploy**

The Google Cloud environment provide all we need to build, test and package our containers in a scalable maner so we are going to use the Google Build. 

**Running Environments**

To provide a simple to use and maintain environment we will use the Google Kubernets Engine (GKE) with Istio to have a full microservices chassi to build upon. 

## Blue/Green and Canary Deployments with Google Cloud, Istio

### Blue/Green diagram
![Blueprint deployment](./blue-green.png)

Follow this guide to implement a blue/green deployment strategy using Google Cloud targeting a polyglot application deployed to an GKE Cluster using Helm. Istio is used to shape traffic to different versions of the same microservice giving full control on what your users see and controlling the flow of releases throughout the pipeline.


### Useful links
---
- [CI&T Website](https://us.ciandt.com/)
- [Google Cloud Build](https://cloud.google.com/cloud-build/)
- [Google Kubernets Engine](https://cloud.google.com/kubernetes-engine/)
- [Istio](http://istio.io)
