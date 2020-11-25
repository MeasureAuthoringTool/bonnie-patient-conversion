The Patients' conversion application converts QDM bonnie patients to FHIR 4 patients.

The system uses spring boot 

To Build:

` mvn clean install`

To run the spring boot application:

`mvn spring-boot:run`

To Build docker (docker must be running) image:

`mvn spring-boot:build-image`

To Run docker image (the version can change):

`docker run -it -p5050:5050 bonnie-patients-conversion:0.0.1-SNAPSHOT`

To upload image to docker hub use this guide:

https://ropenscilabs.github.io/r-docker-tutorial/04-Dockerhub.html

Docker image located here:

`[docker.io/greenemcg/bonnie-patients-conversion]`
 
