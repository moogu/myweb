[![Build Status](https://travis-ci.org/moogu/myweb.svg?branch=master)](https://travis-ci.org/moogu/myweb)   
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=moogu_myweb&metric=alert_status)](https://sonarcloud.io/dashboard?id=moogu_myweb)

# myWeb

This is an example project that can be used as a starting point to create your own Vaadin application with Spring Boot.
It contains all the necessary configuration and some placeholder files to get you started.

The project is a standard Maven project, so you can import it to your IDE of choice. [Read more how to set up a development environment](https://vaadin.com/docs/v14/flow/installing/installing-overview.html) for Vaadin projects (Windows, Linux, macOS). 

This project was created from https://start.vaadin.com.

## Running the Application
There are two ways to run the application:  
 - To run from the command line, use `mvn` and open [http://localhost:8080](http://localhost:8080) in your browser.
 - Another way is to to run the `Application` class directly from your IDE.

#### Intellij IDEA
- On the right side of the window, select Maven --> Plugins--> `spring-boot` --> `spring-boot:run` goal
- Optionally, you can disable tests by clicking on a `Skip Tests mode` blue button.

Clicking on the green run button will start the application.

After the application has started, you can view your it at http://localhost:8080/ in your browser.

## Project structure

- `MainView.java` in `src/main/java` contains the navigation setup. It uses [App Layout](https://vaadin.com/components/vaadin-app-layout).
- `views` package in `src/main/java` contains the server-side Java views of your application.
- `views` folder in `frontend/src/` contains the client-side JavaScript views of your application.

## What next?

[vaadin.com](https://vaadin.com) has lots of material to help you get you started:

- Follow the tutorials in [vaadin.com/tutorials](https://vaadin.com/tutorials). Especially [vaadin.com/tutorials/getting-started-with-flow](https://vaadin.com/tutorials/getting-started-with-flow) is good for getting a grasp of the basic Vaadin concepts.
- Read the documentation in [vaadin.com/docs](https://vaadin.com/docs).
- For a bigger Vaadin application example, check out the Full Stack App starter from [vaadin.com/start](https://vaadin.com/start).

## Deploying using Docker

To build the Dockerized version of the project, run

```
docker build . -t myapp:latest
```

Once the Docker image is correctly built, you can test it locally using

```
docker run -p 8080:8080 myapp:latest
```
