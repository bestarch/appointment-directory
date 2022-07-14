# Sample appointment directory app (for scheduling appointment with doctors)

This application:
* Leverages Java 1.8, Spring Boot 2.6, Maven, Redis, Thymeleaf, JQuery, Chart.js, Bootstrap as a technology stack
* Uses Redis as external session store
* Redis Streams as asynchronous processing engine for rejecting/approving appointments
* Redis Hash for storing appointment model objects
* RediSearch 2.2 redis module for indexing and searching through the JSON objects and aggregating the result for analytic purposes
* _Optional: Can use RedisStack which has RediSearch included in it by default_

![appt_directory (2)](https://user-images.githubusercontent.com/26322220/178896690-c32eebbb-19a3-46bd-b723-00bab92725c9.png)


****Running the application****

1. **Using IDE like STS or IntelliJ**
   <br>
   * Clone the repo https://github.com/bestarch/appointment-directory
   * Switch to branch "basic_demo"
   * Import the code into th IDE of your choice like STS or IntelliJ
   * Spin up a new or existing Redis Enterprise cluster using Redis Enterprise software OR Redis Enterprise Cloud OR run Redis Stack server on your local  machine
   * Open the application.yml file and modify the redis server url, port and password variables. If password is not required, keep the value of spring.redis.auth as 'false'
   
   * *Executing mandatory index scripts*

     Before using the application, connect to the Redis instance using **redis-cli** command line utility and execute following index scripts. This is   necessary for the search feature to work.

   	  > FT.CREATE idx-createdTime-v2 PREFIX 1 "appointment:" SCHEMA createdTime NUMERIC SORTABLE

   * Run the application file 'AppointmentDirectoryApplication.java'
   * open http://localhost:8080
   * Finally shut down the Redis Enterprise server and application server once done
   
2. **Using Kubernetes**
   <br>
   Application can be deployed to Kubernetes cluster as well. For that respective YAML files are included. It contains:
	* the kubernetes deployment, 
	* configmap and 
	* service YAML file for loadbalancer type (for deploying on Cloud)
   _make sure to execute above index scripts on redis server before running the application_
   
3. **Run with docker**
   <br>
   (Install docker runtime if not installed)<br>
   Docker image for this application: **abhishekcoder/appointment-directory:basic_demo**

   _Make sure 
   	_Redis Enterprise server or RedisStack server is already running_
        _execute above index scripts on redis server before running the application_

   Execute the following command to run the application:
   > **docker run -p 127.0.0.1:8080:8080 -e SPRING_REDIS_HOST=<REDIS_URL> -e SPRING_REDIS_PORT=<REDIS_PORT> -e SPRING_REDIS_PASSWORD=<REDIS_PSWD>  abhishekcoder/appointment-directory:basic_demo**

   For instance:
   > **docker run -p 127.0.0.1:8080:8080 -e SPRING_REDIS_HOST=localhost -e SPRING_REDIS_PORT=6379 -e SPRING_REDIS_PASSWORD=e9gydixtEWqN4tYKgRnhUXysXADYJzZ9  abhishekcoder/appointment-directory:basic_demo**

4. **Run with docker compose**
   <br>
   (Install docker runtime if not installed)<br>
   This is the fastest way to spin-up the application and redis server. No need to execute any database scripts. All will be done by docker-compose utility  behind the scene. 
   Just execute the following command to run the application. This will create required docker objects including the needed containers one each for web-app  and redis server. These containers are part of the same private network:
   > **docker compose up**

   Execute the following command to tier down the docker artifacts created above including the private network:
   > **docker compose down**

<hr/>
<hr/>

**Snapshots of the application**

Login page:

<img width="454" alt="image" src="https://user-images.githubusercontent.com/26322220/160120287-ab92bee7-e1e4-4791-a153-a7c9f02480dc.png">

Appointment list page:

![image](https://user-images.githubusercontent.com/26322220/178931542-fd76ff44-bc1a-4f4e-9126-51c8ad4150e1.png)

New appointment:

<img width="1284" alt="image" src="https://user-images.githubusercontent.com/26322220/178891150-efda8629-4dd2-48ff-91ce-2cb8ea1c882b.png">

<hr/>
<hr/>
