[![Inactively Maintained](https://img.shields.io/badge/Maintenance%20Level-Inactively%20Maintained-yellowgreen.svg)](https://gist.github.com/cheerfulstoic/d107229326a01ff0f333a1d3476e068d)

This label here is indicating if the repository is maintained currently. 

I plan to go back to this project at around summer/autumn, where I resolve all of the issues that I have created.

This application implements most, if not all of the 17 security measures mentioned in:

["Towards a Security Benchmark for the Architectural Design of Microservice Applications"](https://dl.acm.org/doi/abs/10.1145/3538969.3543807)

However, 2 of the rules were not implemented yet. (R15 and R17) -> R15 was never intended to be implemented as a service mesh deployment was never planned, thus will never be implemented in this showcase application.

R17 on the other hand is something where I still have to figure out how to implement the hashicorp vault in addition to the spring cloud vault package.
R17 is going to be implemented at some point this year (2023)

# Information
This is a microservice application created with Spring Boot and Angular.

It is supposed to be a showcase on how to implement security features in microservice applications

#### ***PORTS USED IN EVERY SERVICE***

Eureka Service: 8762 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  API Gateway: 8102

Authorization Service: 8977 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Blog Service: 8337

Comment Service: 8188 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  Logging Service : 8739

User Service: 8342 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Token Service: 8333

#### ***PORTS USED FOR DATABASES***
User-ServiceDB: 5433 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  Authorization-Service-DB: 5434

Blog-Service-DB: 5435 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Comment-Service-DB: 5436

RedisDB(RateLimiter): 6739

#### ***PORTS USED FOR DASHBOARD SERVICES***

Prometheus: 9090 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Grafana: 3000

# **Requirements** 

Necessary programs needed to run this microservice application

### *Docker:*

There are multiple Docker/Docker-Compose files that are needed to be executed for this platform to work on one machine. 

### *Java Version*:

The currently used Java Version is 17. 
In IntelliJ the specific version that is set is 
*Coretto 17 // Amazon Coretto version 17.0.4*

As recorded, the application did not work on a Java Version below 11. 
So please, use Java 17

# **Instructions**

### **Windows** (make sure the pathname is not too long, otherwise it won't work)


Copy the Kafka Folder and insert it in to the first directory on the system (C:/) (only windows, to avoid the exception for a pathname that's too long)

Go into Kafka/config/ and adjust the path for the certificates
location on your windows system

Open 2 Terminals

cd into Kafka Folder in both of those terminals

Make sure you use a Java Version equals a version of 17 or higher for Kafka to work

in first terminal enter: 
./bin/windows/zookeeper-server-start.sh config/zookeeper-server-start.sh
in second terminal enter:
./bin/windows/kafka-server-start.sh config/server.properties

Open a new terminal, cd into Code Folder, and execute the start.sh:

if the start.sh aborts at the execution for docker compose 
then in the same directory enter: 

docker compose up

After the docker compose has finished
you can access the platform at https://localhost

The grafana dashboard is located at http://localhost:9001
however should shown as empty as I was not able to import the locally created dashboard accordingly.

The Eureka service is located at https://localhost:8762, so you can see the registered services.

### **Mac OS X**

Open 2 Terminals

cd into Kafka Folder in both of those terminals

Make sure you use a Java Version equals a version of 17 or higher for Kafka to work

cd into Kafka folder

First, chmod +x /bin/zookeeper-server-start.sh, /bin/kafka-run-class.sh and /bin/kafka-server-start.sh
or, chmod 777 /bin/zookeeper-server-start.sh, /bin/kafka-run-class.sh and /bin/kafka-server-start.sh

Then, enter in first terminal: 
./bin/zookeeper-server-start.sh config/zookeeper-server-start.sh

enter in second terminal:
./bin/kafka-server-start.sh config/server.properties (watch that this, does not end suddenly. If it does, delete the content of the logs folder in the kafka folder and restart)

create a new terminal, cd into Code Folder, and execute the start.sh:

the start.sh will most certainly fail after creating all the jars for the applications

manually enter: docker compose up

After the docker compose has finished
you can access the platform at https://localhost

the grafana dashboard is located at http://localhost:9001
however should shown as empty as I was not able to import the locally created dashboard accordingly.

The Eureka service is located at https://localhost:8762, so you can see the registered services.


## The platform
After all the services started, please wait a few minutes
since I created the authorization server on Windows with an AMD processor,
it may not be as performant as I would like it to be on other hardware. 

I had to do this, since the jar creation on Mac OS X for the authorization server
missed files and thus did not issue tokens or expose the jwk set for retrieval 
by other services. 

The image of the authorization server has been pushed to docker hub
which can be located here: 

https://hub.docker.com/layers/2fandev/showcase_application/authorization_service/images/sha256-0fb8c239db73a791f247adf683f12928a48bf8a079d1808a8c75836cd81a9f15?context=repo

Hence, the authorization server has performance issues on Mac OS X 
and takes a few minutes to boot after the docker container has been started.

If after a long time there still is no access_token in your browser cookies,
then please look if the container is not down or paused.

If all the requests are failing it is because the jwt token was not recognized
as valid from the api gateway and is thus throwing an error. For this error, you 
possibly had an access_token already in place. You can verify, that this is
the case, as the requests should have a description on why the service failed
and it is due to the fact, that the signature of the jwt, which is already
present in your cookies tab, is not correct and cannot be validated (Code 401).
In the developer console, please
delete that cookie and refresh the site, then a new access_token should be visible
in your cookies.

Otherwise, if the failing requests 
are displaying a CORS error, then please use google chrome and 
install this plugin: 

https://chrome.google.com/webstore/detail/allow-cors-access-control/lhobafahddgcelffkeicbaginigeejlf

Then you only have to activate as long as you are at https://localhost/ and can delete it, when 
not using this showcase application anymore. 

After all the services are started, please click the register button 
and create an Account. For demonstration purposes, the username with the name "admin" 
gets an authority of "ADMIN" while any other account is getting "USER". Logged out users 
of the platform get an authority of "ANONYMOUS". 

After that the platform is accessible and usable.
There will be a loading circle as long as there are no blogs 
visible and will stop being there, after 1 blog has been posted.

In the developer tools, under application and under cookies you can see, there should be 
a value for access_token. Without logging in, you are getting a anonymous token. 
To see the contents of this JWT, you can copy the value and and paste it into https://jwt.io

Please, if your hardware is able to handle the amount of servers, 
register with an account and login. After login, there should be 2 more tokens in your cookies,
which would be a replaced access token and a refresh token. Feel free to enter the access_token again at https://jwt.io . You should see, that the values have changed to the currently logged in user. The refresh token however, cannot be read by this site. 

## Future

This is still not finished, and I will try to keep it up to date in the next few years. 
Anyone who is interested in working in this, I have already created issues and plan to 
fix the issues whenever I have time.
