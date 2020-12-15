 
DART is a user-friendly, open source software tool to provide practitioners with all relevant information to support confident genetic discovery in compliance with emerging community standards.

# Architecture
![Architecture diagram](./Architecture.jpeg?raw=true "Architecture")

DART is composed by different systems:
 - Back End:
 	- Variant data is stored in a mongoDB instance to allow a flexible data format together with BED files (panels) data and Coverage data
 	- The list of loaded files together with users, permissions and reports are stored in a MySQL server
 - Core:
 	- The backend is accessed through the service layer which communicates with mongoDB using the Mongo Java driver and with MySQL using Hibernate
 	- The core of the DART web app is the controller layer which manages the communication between the consumer and the service layer
 - API:	
 	- The data is accessed via a REST API which communicates to the Back-End via the controller
 - Core+API are packaged together into a single EAR file
 - Web application:
 	- The application exposed to the user is written in JavaScript using the Angular framework. The app is packaged in a WAR file
 - Cache:
 	- The caching of the queries and query results is performed via Infinispan and cache data is persisted on the MySQL database
 - Web server:
 	- The application (EAR + WAR) is deployed on a JBOSS wildfly application server. Static content is served by the undertow subsystem and security is managed by the elytron component.
 	- All the configuration is deployed in the standalone.xml file                                                                                                                                                                                                                                                                                       
 - Loader:
   	- The loader is a java executable which reads and parses the VCF files and inserts the variants into the Back-End through Java Remote Procedure Calls
   	
# Quick installation

## PRE-REQUISITES
- You must have **DOCKER** installed and running on your machine: for LINUX please be sure that both the docker and docker-compose packages are installed

## INSTALLATION
Download the lastet release binary [DART-server-1.0.0-install.tar.gz]( https://github.com/DanieleBarreca/BSF_DART/releases/download/1.0.0/DART-server-1.0.0-install.tar.gz) and extract it:
```
tar -xzvf DART-server-1.0.0-install.tar.gz 
```
**DO NOT** remove the installation folder DART-server-1.0.0 since all the data resides here!

In the DATA folder of the installation folder you will find:
 - A log folder where the application server logs are written
 - A mongo folder where the mongodb files are written
 - A mysql folder where the mysql files are written
 - A projects folder where your project data reside
 
## USAGE

### START the application server
To start the application server you can use the start script:
```
bash DART-server-1.0.0/start.sh
```
Please note that the server log is under DATA/log/server.log. The server can take some time before being up and running.


### Install demo data
To load data on your local installation of DART you should create a project in your installation folder under DATA/projects and then run the loader (the application server must be running - see usage instructions below)

1. To download and set our demo trio project (https://doi.org/10.5281/zenodo.4314874), you can use the following:

```
cd DART-server-1.0.0
wget --no-http-keep-alive https://zenodo.org/record/4320863/files/DART_Demo_Trio.tar.gz
tar -xzf DART_Demo_Trio.tar.gz
```
2. Once the project is set in the DATA/projects folder you can load it in DART using the command
```
docker exec -it dart-server './bin/dart_loader/loader.sh' 'DART_Demo_Trio'
```

### USE the tool
Open your browser (tested on Google Chrome on MacOS) on [localhost:8080](http://localhost:8080) and login using the credentials:

 - User: *DART_user*
 - Password: *changeme*

You can refer to the [online documentation](https://danielebarreca.github.io/BSF_DART/) for usage details.

### STOP the application server
To stop the application server you can use the stop script:
```
bash DART-server-1.0.0/stop.sh
```

## UN-INSTALLATION
1. Remove all the docker images (container are removed by the stop script)
```
docker image rm org.open.medgen/dart-server:1.0.0_local
```
2. Remove all the data stored locally
```
rm -r DART-server-1.0.0
```



# Build from repository

## Prerequisites
 - [npm](https://www.npmjs.com/)
 - [nodejs](https://nodejs.org/)
 - [mvn](https://maven.apache.org/) (min. 3.3.9)
 - [openJDK 8](https://openjdk.java.net/)
 - [git](https://git-scm.com/)
 - Docker and Docker compose
 
## Build profiles

To allow for a flexible development different maven build profiles are available:
 - server: packages the wildfly application server and deploys the standalone.xml configuration for DART (compile phase) 	
 - front-end: packages and deploys the front-end WAR (angular app) (package phase)
 - back-end: packages and deploys the back-end EAR (JAVA REST API + Core) (package phase)
 - loader: packages and deploys the loader application and script (deployed in the bin/dart_loader folder of the application server) (package phase)
 - package-docker: creates a docker image for the server and the installation binary
 
The wildfly web server is built and configured in DART-server/target/DART_install.

## Configuration details
The pom.xml file of the DART project contains `default` values for all username / passwords.
Most values can be overwritten using the specified `environment variables` further described below.
Properties without an environment variable can __not__ be changed after compilation (running mvn install ...).

| pom property name | environment variable |  Description  |
| :---------------- | :------------------- | :------------ |
| dart.application.user | DART_LOADER_USER | Dart login user name  |
| dart.application.user.group  | DART_LOADER_GROUP | Dart user group  |
| dart.application.user.first_name | - | Dart user first name |
| dart.application.user.last_name | - | Dart user last name |
| dart.application.user.email | - | Dart user email |
| dart.application.user.password | DART_LOADER_PASSWORD | Dart user password |
| dart.wildfly.admin.user | DART_WILDFLY_ADMIN_USER | Wildfly admin user |
| dart.wildfly.admin.password | DART_WILDFLY_ADMIN_PWD | Wildfly admin password |
| dart.mongo.server | DART_MONGO_SERVER | MongoDB DART server URI |
| dart.mongo.admin.db | DART_MONGO_ADMIN_DB | MongoDB admin db |
| dart.mongo.admin.user | DART_MONGO_ADMIN_USER | MongoDB admin user |
| dart.mongo.admin.password | DART_MONGO_ADMIN_PWD | MongoDB admin password |
| dart.mongo.db | DART_MONGO_DB | MongoDB DART database name |
| dart.mongo.user | DART_MONGO_USER | MongoDB DART user |
| dart.mongo.password | DART_MONGO_PWD | MongoDB DART user password |
| dart.mongo.query_limit | DART_MONGO_QUERY_LIMIT | MongoDB query result limit |
| dart.sql.server | DART_SQL_SERVER | MySQL server URI |
| dart.sql.params | DART_SQL_PARAMS | MySQL connection parameters | 
| dart.sql.db | DART_SQL_DB | MySQL database name |
| dart.sql.admin.user | DART_SQL_ADMIN_USER | MySQL admin user |
| dart.sql.admin.password | DART_SQL_ADMIN_PWD | MySQL admin pwd |
| dart.sql.user | DART_SQL_USER | MySQL DART user |
| dart.sql.password | DART_SQL_PWD | MySQL DART password |
| dart.web.server.production | - | DART running in production (true/false) |
| dart.web.server.base.url | - | DART base URL e.g. `localhost:8080` |
| dart.web.server.protocol | - | DART server protocol e.g. `http` |
| dart.web.server.api.url | - | DART web url api `${dart.web.server.protocol}://${dart.web.server.base.url}/DART-API/` |
| dart.web.server.static.url | - | DART static web content url e.g. `${dart.web.server.protocol}://${dart.web.server.base.url}/static/` | 
| dart.web.server.url | - | DART final web API URL e.g. `http://localhost:8080/DART-API/` |
| dart.web.base.href | - | DART path in URL e.g. `/` or `/my/personal/research/dart/` |

## Build instructions

Download this repository `git clone https://github.com/DanieleBarreca/BSF_DART.git`

## 1. Build DART 
  - Go to the source code root: `cd BSF_DART`
  - Decide on the URL, where DART will be accessible from (`dev` profile points to localhost:8080/)
  - Prepare and package all resources in different modes

### 1.1. Build DART with default values 
  - Use default values using `mvn clean install -P server,front-end,back-end,loader,package-docker` 

### 1.2. Build DART with custom values 
  - Customize your final server URLs by providing properties: 
```
mvn clean install -P server,front-end,back-end,loader,package-docker \
 -Ddart.web.server.production=true \
 -Ddart.web.server.url=http://my-dart.int.cemm.at:8080/DART-API/ \
 -Ddart.web.base.href=/
 ```

## 2. Run DART
You can run DART from the `DART-server/target/DART_install` via Docker using the start.sh and stop.sh scripts (see USAGE section).

Alternatively, you can start mongodb and mysql using the `DART-server/target/DART_docker/docker-compose-Dev.yml`
 docker compose file and run the server manually from the `DART-server/target/DART_docker/wildfly_custom_17` folder.
 In this case it is important to make sure that both the `DART_MONGO_SERVER` and `DART_SQL_SERVER` variables in the `DART_server/target/DART_install/default.env` file point to `localhost`.

### 1. Set the environment
  ```
  cd DART_server/target/DART_docker
  
  docker-compose -f docker-compose-Dev.yml up -d
  
  set -a
  source ../DART_install/default.env
  set +a
  ``` 
### 2. Manually run the server:
 ```
 ## add admin user + start server
 bash dart-entry-point.sh
 ```
 
 In this case the loader is located in the `DART-server/target/DART_docker/wildfly_custom_17/bin/dart_loader` folder and projects are served from the `DART-server/target/DART_docker/wildfly_custom_17/static-content` folder
 
# Resources used in this work 
The Loader uses resources from the following resources to annotate somatic variants:
 - [CIViC](https://civicdb.org/home)   Griffith, M., Spies, N., Krysiak, K. et al. CIViC is a community knowledgebase for expert crowdsourcing the clinical interpretation of variants in cancer.  [Nat. Genet 49, 170-174 (2017)](https://doi.org/10.1038/ng.3774)
 - [Cancer Hotspots](http://www.cancerhotspots.org/#/about) Chang, M., Asthana, S., Gao, S. et al. Identifying recurrent mutations in cancer reveals widespread lineage diversity and mutational specificity. [Nat Biotechnol 34, 155–163 (2016)](https://doi.org/10.1038/nbt.3391)
 - [3D Hotspots](http://www.3dhotspots.org/#/home) Gao J, Chang MT, Johnsen HC, Gao SP, Sylvester BE, Sumer SO, Zhang H, Solit DB, Taylor BS, Schultz N, Sander C. 3D clusters of somatic mutations in cancer reveal numerous rare mutations as functional targets. [Genome Med. 2017 Jan 23;9(1):4](https://doi.org/10.1186/s13073-016-0393-x)
 
The [Orphanet Rare Disease Ontology (ORDO)](http://www.orphadata.org/) is loaded in the  MySQL DB as reference ontology for disease annotation.

## Libraries and 3rd party software
All the libraries used in the build are and their licenses are listed in the license folder. 
