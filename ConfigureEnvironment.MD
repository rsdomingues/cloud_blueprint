![CI&T Logo](https://us.ciandt.com/themes/custom/ciandt/images/ciandt-logo-for-light.png)


### Building the application
---






### Deploy GKE Environment
---

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