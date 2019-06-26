# Github Google Cloud Blueprint for FinServ

![](https://cdn-images-1.medium.com/max/2048/1*QS_c6vAjhA6chOusy2mELA.png)

Microservice and DevOps go hand in hand when we are talking about the speed of development. In this simple article, I will try to create a blueprint to help you kickstart software development workflow on Google Cloud Platform and Github

We need to build a development flow that enables developers to have fast feedback from their work and connect that work in a secure and fast way to the production environment.

What that flow should look like?

The following diagram shows the main responsibilities, we will explore in more details each phase and step of the process.

![](https://cdn-images-1.medium.com/max/2000/1*TnawOxDhSYmKtmHq7SsybA.png)

## Technical requirements

**The application (test subject)**

For this DevOps workflow, we need a sample app, for this, we are going to use the [FooApp](https://github.com/rsdomingues/cloud_blueprint/tree/master/fooapp), which is a simple cloud-native java application build on top of the [Spring Cloud Framework](https://spring.io/projects/spring-cloud). Feel free to explore it, because it has some example of automated testing, clean architecture, and some other stuff.

**Development process**

To give our developer the ability to focus on the application management part we need to build a fully automated pipeline that will take our code from the SCM repository to production.

For quality and security, we are going to insert “control gates” on the pipeline for our binaries, so that we can ensure that the “built and tested” application is deployed successfully in a risk-free way to the production environment.

**Source Control Management**

One big important element to team productivity is the SCM process, to enable fast and secure development. There are two main approaches used by most companies, Gitflow and Trunk Based Development (TDB). While TDB provides a much faster approach, for most teams it is difficult to adopt it, due to culture changes involved in the way of work.

All things considered, in this case, I chose to use the Gitflow approach, simply because it has a better chance to help more people. Even using Gitflow I highly recommend working with a reduced model of branches and planning on how to change it to trunk based development approach, because of the speed and connection it provides between development and production.

**Packaging and Deploy**

For the application packaging, my belief is that modern application should use Containers, they provide a real-world proved way of paring production and development, reducing issues with snowflake environments. Besides that most cloud providers have some sort of Platform as a Service for containers which gives you a good way of simplifying your production environment.

Containers are also a way of bridging the gap between development and operation, giving developers an open and reliable way to select the best technology for the problem and still give the operation team a simple way to manage everything.

**Running Environments**

Manually managing containers is a time-consuming hard task, that requires a lot of effort. There are several options for container manager in the real world, like Kubernetes, Rancher, Open-Shift and Apache Mesos to name a few. These tools provide a simpler, faster and more secure way of working with containers.

**Deployment Strategy**

Going down for maintenance or deployment is always a bad release strategy. In today's digital world that is even worst. There are several alternatives the enables us to have a less UX impactful release, such as Blue/Gren deployment, Canary Release, Rolling updates to name a few.

In this scenario, I will choose the Blue/Green deployment, there are a lot of tools and support for this type of approach, it is a simple approach and it enables us to have a fast and reliable rollback out of the box too.

Stay tuned for the next piece, where I am going to guide you through a step-by-step example on how to build your workflow.

### Useful links
---
- [CI&T Website](https://us.ciandt.com/)
- [Google Cloud Build](https://cloud.google.com/cloud-build/)
- [Google Kubernets Engine](https://cloud.google.com/kubernetes-engine/)
- [Istio](http://istio.io)
