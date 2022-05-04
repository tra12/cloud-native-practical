# Lab 03 - Podman

We've got a bare minimum application working, time to containerize it. This will serve us well when deploying to any container orchastration platform.

We'll install a few prerequisites using HomeBrew package manager for MacOS, if you don't have it yet see:

https://brew.sh/

## Install Podman

If you haven't already, we need to install Docker:
```
brew install podman
```

Start the Podman-managed VM:

```
podman machine init
podman machine start
```

Ensure that everything is working:

```
podman version

---

Client:       Podman Engine
Version:      4.0.3
API Version:  4.0.3
Go Version:   go1.18
Built:        Fri Apr  1 17:28:59 2022
OS/Arch:      darwin/amd64

Server:       Podman Engine
Version:      4.0.2
API Version:  4.0.2
Go Version:   go1.16.14
Built:        Thu Mar  3 15:56:56 2022
OS/Arch:      linux/amd64
```

## Containerize our application

Now we're going to package our application as a container image. Add a Dockerfile to the module root, alongside the pom.xml:

```
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/*.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
```

Before we build this Dockerfile, make sure the latest code is packaged by explicitly doing a Maven build:

```
cd shopping-list
mvn package
```

Now do the Podman build:

```
podman build -t shopping-list:0.0.1-SNAPSHOT .
```

Run our Docker image, we're mapping our 8080 port to 8081 to avoid a possible conflicting running application:

```
podman run -p 8081:8080 shopping-list:0.0.1-SNAPSHOT
```

Verify it's up and running:

http://localhost:8081/actuator/health

A deep understanding of Docker is not needed for now, but try to research what is actually going on while running these commands.

## Commit and tag your work

Make sure to add, commit and push all your files at least once at the end of every lab. After the lab has been completed completely please tag it with the appropriate lab number:

````
git tag -a lab03 -m "lab03"
````