# Sample appointment directory app (for scheduling appointment with doctors)

This application:
* Leverages Java 1.8, Spring Boot 2.6, Maven, Redis, Thymeleaf as a technology stack
* Uses Redis as external session store
* Redis Streams as asynchronous processing engine for rejecting/approving appointments
* RedisJSON 2.0 redis module for storing appointment model objects
* RediSearch 2.2 redis module for indexing and searching through the JSON objects and aggregating the result for analytic purposes
* _Optional: Can use RedisStack which has RediSearch and RediJSON included in it by default_

Application can be deployed to Kubernetes env as well. It contains:
* the kubernetes deployment, 
* configmap and 
* services YAML files.
 
This uses service type loadbalancer with annotation type NLB deployed in AWS EKS.


**Snapshots of the application**

Login page:

<img width="454" alt="image" src="https://user-images.githubusercontent.com/26322220/160120287-ab92bee7-e1e4-4791-a153-a7c9f02480dc.png">

Appointment list page:

<img width="1093" alt="Screenshot 2022-04-26 at 8 31 07 AM" src="https://user-images.githubusercontent.com/26322220/165211956-0bafcd71-0c8e-4d32-8067-90527a89af82.png">

New appointment:

<img width="674" alt="Screenshot 2022-04-26 at 8 31 32 AM" src="https://user-images.githubusercontent.com/26322220/165211988-18d91d8b-40a0-4e1f-bd77-fa5ddae37b42.png">

**Run with docker**

_Make sure redis server is running already_: You may use Redis Enterprise server to point your application to the application.

Execute the following command to run the application:
> **docker run -p 127.0.0.1:8080:8080 -e SPRING_REDIS_HOST=<REDIS_URL> -e SPRING_REDIS_PORT=<REDIS_PORT> -e SPRING_REDIS_PASSWORD=<REDIS_PSWD> abhishekcoder/appointment-directory:latest**

For instance:
> **docker run -p 127.0.0.1:8080:8080 -e SPRING_REDIS_HOST=localhost -e SPRING_REDIS_PORT=6379 -e SPRING_REDIS_PASSWORD=e9gydixtEWqN4tYKgRnhUXysXADYJzZ9 abhishekcoder/appointment-directory:latest**

<hr/>

**Run with docker compose**

Execute the following command to run the application. This will create 2 containers one for web app and another for redis server. The two containers are part of the same private network:
> **docker compose up**

Execute the following command to destroy the above 2 containers in the private network:
> **docker compose down**
