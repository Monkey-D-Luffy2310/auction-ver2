# AchauAuction Project

AchauAuction project is based on Java Spring that can run alone , in servlet container (tomcat,jetty,etc ..) , or in appengine ( include both Standard and Flexible enviroment)


## Getting Started
### Clone this repository
```
git clone https://github.com/acgroupvn/AchauAuction.git
```

### Prerequisites
- Jdk 8+
- Maven or maven wrapper 
- Gcloud tools ( to deploy to GCP )

### Run alone
```
mvn clean package spring-boot:run
```

### Run in appengine local development
```
mvn clean package appengine:run
```


### Deploy to appengine Standard


```
mvn clean package appengine:deploy

```

### Deploy to appengine Flexible

Create file app.yaml and deploy

```
mvn clean package appengine:deploy

```


## Build Docker image 

```
mvn jib:build
```
