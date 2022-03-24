# Sample appointment directory for users using Redis 

This application:
* leverages Redis as external session store
* Redis Streams as asynchronous processing engine for rejecting/approving appointments

This also contains:
the kubernetes deployment, 
configmap and 
services YAML files.
 
This uses service type loadbalancer with annotation type NLB deployed in AWS EKS.
