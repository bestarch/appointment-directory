# Sample appointment directory for users using Redis 

This application:
* leverages Redis as external session store
* Redis Streams as asynchronous processing engine for rejecting/approving appointments

Application can be deployed to Kubernetes env as well. It contains:
* the kubernetes deployment, 
* configmap and 
* services YAML files.
 
This uses service type loadbalancer with annotation type NLB deployed in AWS EKS.


**Snapshots of the application**

Login page:

<img width="454" alt="image" src="https://user-images.githubusercontent.com/26322220/160120287-ab92bee7-e1e4-4791-a153-a7c9f02480dc.png">

Appointment list page:

<img width="1277" alt="image" src="https://user-images.githubusercontent.com/26322220/160120413-08bffc77-8ea6-4ba1-9492-579507f34ca2.png">

New appointment:

<img width="1002" alt="image" src="https://user-images.githubusercontent.com/26322220/160120467-ba34b41f-a848-4c3f-ab4a-ca2100bbf681.png">

**Run with docker**

_Make sure redis server is running already_: You may use Redis Enterprise server to point your application to the application.

Execute the following command to run the application:
> **docker run -p 127.0.0.1:8080:8080 -e SPRING_REDIS_HOST=<REDIS_URL> -e SPRING_REDIS_PORT=<REDIS_PORT> -e SPRING_REDIS_PASSWORD=<REDIS_PSWD> abhishekcoder/appointment-directory:latest**

For instance:
> **docker run -p 127.0.0.1:8080:8080 -e SPRING_REDIS_HOST=localhost -e SPRING_REDIS_PORT=6379 -e SPRING_REDIS_PASSWORD=e9gydixtEWqN4tYKgRnhUXysXADYJzZ9 abhishekcoder/appointment-directory:latest**

**Run with docker compose**

Execute the following command to run the application. This will create 2 containers one for web app and another for redis server. The two containers are part of the same private network:
> **docker compose up**

Execute the following command to destroy the above 2 containers in the private network:
> **docker compose up**
