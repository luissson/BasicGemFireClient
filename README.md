# BasicGemFireClient

# Project Description
This project provides a basic example of a TLS-enabled GemFire client that's intended to run in Kubernetes and communicate with a
GemFire for Kubernetes Cluster. See [[Link to Blog]] 

# Build and Run

To build the client, run from the root of the repository

`mvn clean compile assembly:single`

This produces a single executable jar file with dependencies included in the generated 'target' directory.

To run the client, the following environment variables must be set to correct values

| Env Var          | Value                                             |
|------------------|---------------------------------------------------|
| LOCATOR_HOST     | IP or Hostname for a locator in a GemFire cluster |
| TRUST_STORE_PSWD | Password for the certificate trust store          |

then execute 

`java -jar target/gemfireclient-1.0-SNAPSHOT-jar-with-dependencies.jar`

# Deploy

Pre-requisites:
* Kubernetes environment
* A running GemFire for Kubernetes cluster with TLS and a region named 'example-region'

To deploy the client to Kubernetes, create a container image (to be used by the Pod) using the provided Dockerfile.

`docker build --tag gfclient .`

Re-tag and push the generated image to the Docker repository of your choosing

`docker tag gfclient <<IMAGE-REPOSITORY>>/gfclient`

`docker push <<IMAGE-REPOSITORY>>/gfclient`


Create a certificate using the default GemFire for Kubernetes certificate issuer for the client. 
The Certificate will be generated by `cert-manager`, and the cert data will be placed in a Secret.
The client Pod will then reference the Secret and place the cert data on its file system.

First create the [Kubernetes Secret](./k8s/client-secret.yaml)

`kubectl apply -f ./k8s/client-secret.yaml`

then create the [Certificate](./k8s/client-cert.yaml).

`kubectl apply -f ./k8s/client-cert.yaml`

and lastly, create the [Java application Pod](./k8s/gfclient.yaml); 
substituting `<<IMAGE-REPOSITORY>>` where appropriate

`kubectl apply -f ./k8s/gfclient.yaml`

Once running, the client Pod will connect to the GemFire cluster, execute puts, followed by gets and then exit.
