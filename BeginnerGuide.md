# **ABSOLUTE BEGINNERS GUIDE**

## **OWASP WRONGSECRETS**

Basically, [*WrongSecrets*](https://owasp.org/www-project-wrongsecrets/) is an application which teaches how to not store secrets by offering challenges to the user, which helps the user to Self-reflect and correct those mistakes.

---

### *What are the things you need to know?*

Don’t worry too much if you do not understand the code just reach out to the [*community*](https://app.slack.com/client/T04T40NHX/C02KQ7D9XHR), they’ll help you for sure.
Here is some basic Glossary.

1. **Secrets:**  
   [*Secrets*](https://delinea.com/what-is/application-secrets) refer to a private piece of information that acts as a key to unlock protected resources or sensitive information in tools, applications, containers, and cloud-native environments.



2. **Maven**  
   [*Maven*](https://maven.apache.org/what-is-maven.html) is automation tool chiefly used for Java-based projects, helping to download dependencies, which refers to the libraries or **JAR** files. The tool helps get the right JAR files for each project as there may be different versions of separate packages.<br/>**JAR** stands for **J**ava **AR**chive. Basically, a ZIP file format.


3. **POM- Project Object Model**  
   [*POM*](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html#:~:text=Available%20Variables-,What%20is%20a%20POM%3F,default%20values%20for%20most%20projects.) is the fundamental unit of work in Maven. It is an **XML** file that contains information about the project and configuration details used by Maven to build the project.



4. **XML**  
   [*XML*](https://en.wikipedia.org/wiki/XML) is a markup language that provides rules to define any data. Unlike other programming languages, XML **cannot** perform computing operations by itself. Instead, any programming language or software can be implemented for structured data management.



5. **Spring Boot**  
   [*Spring boot*](https://spring.io/projects/spring-boot) is an open-source Java-based framework used to create a micro-Service. Basically, It helps developers create applications that just run. Specifically, it lets you create **standalone** applications that run on their own, without relying on an external web server.



6. **Lombok**  
   [*Lombok*](https://projectlombok.org/) is a project that offers various annotations aimed at replacing Java code that is well known for being boilerplate, repetitive, or tedious to write.<br/>

---

## **PREREQUISITES**
1. **Docker**  
   [*Docker*](https://www.docker.com/) is a software platform that allows you to build, test, and deploy applications quickly. Docker packages software into standardized units called **containers** that have everything the software needs to run including libraries, system tools, code, and runtime.


2. **Node.Js**  
   [*Node.Js*](https://nodejs.org/en/) is an open-source, cross-platform JavaScript **runtime environment** and library for running web applications outside the client's browser.


3. **JDK-19**  
   [*JDK*](https://www.oracle.com/java/technologies/javase/jdk19-archive-downloads.html) includes tools useful for developing and testing programs written in the Java programming language.


3. **IntelliJ IDEA**  
   [*IntelliJ IDEA*](https://www.jetbrains.com/idea/download/#section=windows) is an integrated development environment basically an **IDE** written in Java for developing computer software written in Java, Kotlin, Groovy, and other JVM-based languages.


4. **GitHub Desktop**  
   [*GitHub Desktop*](https://desktop.github.com/) is an application that enables you to interact with GitHub using a GUI instead of the command line or a web browser.     (*Not Mandatory but it is recommended for beginners*)

---

## **How to get started with the project  in IntelliJ IDEA**

- ### **Step 1: Fork the Project**.
  Navigate to the landing page of the repository in your web browser and click on the ***Fork*** button on the repository’s home page.  
  A forked copy of that Git repository will be added to your personal GitHub.  
  ![](https://user-images.githubusercontent.com/119479391/212522620-a51c2501-6dff-4d77-b449-9889eea4517b.png)

- ### **Step 2: Clone the Project.**
  A **clone** is a full copy of a repository, including all logging and versions of files.  
  To ***clone*** the Project to your local desktop by clicking on the button as shown below.  
  ![](https://user-images.githubusercontent.com/119479391/212522626-868d984f-20a5-4502-bbea-5787dd2b18af.png)

- ### **Step 3: Open the Project using IntelliJ IDEA**
    - ***Open*** the Cloned Project using IntelliJ IDEA by clicking on the button as shown below.  
      ![](https://user-images.githubusercontent.com/119479391/212522635-f776300b-b2d2-4869-8c6f-a1d76ffceb99.png)

    - **Wait** till the Project Loads.  
      ![](https://user-images.githubusercontent.com/119479391/212522646-852ebde9-74b1-455b-83fe-ad260bc9b8a0.png)

- ### **Step 4: Setup.**
    - Open Settings by pressing ***Ctrl+Alt+S***  
      ![](https://user-images.githubusercontent.com/119479391/212522659-2035f1f0-15de-4ae0-a6bd-a976d420f9d1.png)

    - Follow the path ***IDE settings>Language & Frameworks > Lombok*** and then click on ***Lombok.***  
      ![](https://user-images.githubusercontent.com/119479391/212522661-8529fb75-f547-4753-b5b3-af1bcaa5bcf3.png)

    - Make sure that the ***Lombok processing*** is enabled.  
      ![](https://user-images.githubusercontent.com/119479391/212544060-584a266b-9f22-4a16-a9d6-1a9531c1d4e4.png)

- ### **Step 5: Reload the project**
    - Open the ***Maven*** Tab  
      ![](https://user-images.githubusercontent.com/119479391/212522678-4d2f4953-9bc7-4fdb-ab40-7739e13e4ede.png)

    - Press the ***Reload*** button as shown below and allow the project to Reload.   
      ![](https://user-images.githubusercontent.com/119479391/212522686-dd263257-972e-4e41-a0d5-fac630f3510f.png)

- ### **Step 6: Running the Project.**
    - Open the ***WrongSecretsApplication*** by following the path ***main>java>org.owasp.wrongsecrets>WrongSecretApplication***.  
      ![](https://user-images.githubusercontent.com/119479391/212522695-3bb435b2-e57f-44ee-a9d5-f583a53e15f1.png)
    - Press ***Shift+F10*** to run the application, this will open up the ***Run/Debug Configurations Menu.***  
      ![](https://user-images.githubusercontent.com/119479391/212522690-0e8e50eb-9c40-42ab-a7cb-f3cc6fed02ae.png)

- ### **Step 7: Setting up Configurations.**
    -  Select ***Edit configuration templates*** then select ***Application*** section.  
       ![](https://user-images.githubusercontent.com/119479391/212778146-162ff060-8e37-448e-8e04-9cbb803ee9f5.png)
    - There under the ***Application*** section click on the button shown below.  
      ![](https://user-images.githubusercontent.com/119479391/212889483-55e98365-aab9-432e-9c5c-177c0484cb16.png)
    - ***Select*** all the fields that are Selected in the below picture.  
      ![](https://user-images.githubusercontent.com/119479391/212891550-0ded65d2-f78d-4a83-82bf-c4bd44a07f71.png)
    - ***Fill*** all the fields as shown below.   
      ![](https://user-images.githubusercontent.com/119479391/212888252-3d554893-3c11-484c-9f26-84c34b0301e7.png)
    - Again press ***Shift+F10*** which runs the Application.  
      ![](https://user-images.githubusercontent.com/119479391/212522690-0e8e50eb-9c40-42ab-a7cb-f3cc6fed02ae.png)

    - ### **There you have it, ***WrongSecretsApplication*** running successfully.**
      Here is a *preview* on how does it look after successfully running the Application.  
      **Note:** Running the Application doesn't open any kind of ***GUI***, it only initializes the ***local webserver*** that you can open via a ***browser.***    
      <img width="1445" alt="image (1)" src="https://user-images.githubusercontent.com/119479391/214242239-f8d3ed69-6d03-44de-8768-2bc1be8ef3ef.png">


