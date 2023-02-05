# Contributing

[![GitHub contributors](https://img.shields.io/github/contributors/OWASP/wrongsecrets.svg)](https://github.com/OWASP/wrongsecrets/graphs/contributors)
![GitHub issues by-label "help wanted"](https://img.shields.io/github/issues/OWASP/wrongsecrets/help%20wanted.svg)

This document describes how you can contribute to WrongSecrets. Please read it carefully.

**Table of Contents**

-   [How to Contribute to the Project](#how-to-contribute-to-the-project)
-   [How to set up your Contributor Environment](#how-to-set-up-your-contributor-environment)
-   [How to get your PR Accepted](#how-to-get-your-pr-accepted)
-   [Beginner Guide](#beginner-guide)
    -   [About the Project](#OWASP-WrongSecrets)
    -   [Prerequisites](#Prerequisites)
    -   [Pictorial Guide on how to get started with the project in IntelliJ IDEA](#How-to-get-started-with-the-project-in-IntelliJ-IDEA)

## How to Contribute to the project

There are a couple of ways on how you can contribute to the project:

-   **File [issues](https://github.com/OWASP/wrongsecrets/issues "WrongSecret Issues")** for missing content or errors. Explain what you think is missing and give a suggestion as to where it could be added.
-   **Create a [pull request (PR)](https://github.com/OWASP/wrongsecrets/pulls "Create a pull request")**. This is a direct contribution to the project and may be merged after review. You should ideally [create an issue](https://github.com/OWASP/wrongsecrets/issues "WrongSecret Issues") for any PR you would like to submit, as we can first review the merit of the PR and avoid any unnecessary work. This is of course not needed for small modifications such as correcting typos.
-   **Promote us by giving us a Star or share information via social media**.

## How to get your PR accepted

Your PR is valuable to us, and to make sure we can integrate it smoothly, we have a few items for you to consider. In short:
The minimum requirements for code contributions are:

1. The code _must_ be compliant with the configured pre-commit hooks, and Checkstyle and PMD rules.
2. All new and changed code _should_ have a corresponding unit and/or integration test.
3. New and changed lessons _must_ have a corresponding integration test.
4. [Status checks](https://docs.github.com/en/github/collaborating-with-pull-requests/collaborating-on-repositories-with-code-quality-features/about-status-checks) should pass for your last commit.

Additionally, the following guidelines can help:

### Keep your pull requests limited to a single issue

Pull requests should be as small/atomic as possible. Large, wide-sweeping changes in a pull request will be **rejected**, with comments to isolate the specific code in your pull request. Some examples:

-   If you are making spelling corrections in the docs, don't modify other files.
-   If you are adding new functions don't '_cleanup_' unrelated functions. That cleanup belongs in another pull request.

### Write a good commit message

-   Explain why you make the changes. [More infos about a good commit message.](https://betterprogramming.pub/stop-writing-bad-commit-messages-8df79517177d)

-   If you fix an issue with your commit, please close the issue by [adding one of the keywords and the issue number](https://docs.github.com/en/issues/tracking-your-work-with-issues/linking-a-pull-request-to-an-issue) to your commit message.

    For example: `Fix #545` or `Closes #10`

## How to set up your Contributor Environment

1. Create a GitHub account. Multiple different GitHub subscription plans are available, but you only need a free one. Follow [these steps](https://help.github.com/en/articles/signing-up-for-a-new-github-account "Signing up for a new GitHub account") to set up your account.
2. Fork the repository. Creating a fork means creating a copy of the repository on your own account, which you can modify without any impact on this repository. GitHub has an [article that describes all the needed steps](https://help.github.com/en/articles/fork-a-repo "Fork a repo").
3. Clone your own repository to your host computer so that you can make modifications. If you followed the GitHub tutorial from step 2, you have already done this.
4. Go to the newly cloned directory "wrongsecrets" and add the remote upstream repository:

    ```bash
    $ git remote -v
    origin git@github.com:<your Github handle>/wrongsecrets.git (fetch)
    origin git@github.com:<your Github handle>/wrongsecrets.git (push)

    $ git remote add upstream git@github.com:OWASP/wrongsecrets.git

    $ git remote -v
    origin git@github.com:<your Github handle>/wrongsecrets.git (fetch)
    origin git@github.com:<your Github handle>/wrongsecrets.git (push)
    upstream git@github.com:OWASP/wrongsecrets.git (fetch)
    upstream git@github.com:OWASP/wrongsecrets.git (push)
    ```

   See also the GitHub documentation on "[Configuring a remote for a fork](https://docs.github.com/en/free-pro-team@latest/github/collaborating-with-issues-and-pull-requests/configuring-a-remote-for-a-fork "Configuring a remote for a fork")".

5. Choose what to work on, based on any of the outstanding [issues](https://github.com/OWASP/wrongsecrets/issues "WrongSecrets Issues").
6. Create a branch so that you can cleanly work on the chosen issue: `git checkout -b FixingIssue66`
7. Open your favorite editor and start making modifications. We recommend using the [IntelliJ Idea](https://www.jetbrains.com/idea/).
8. After your modifications are done, push them to your forked repository. This can be done by executing the command `git add MYFILE` for every file you have modified, followed by `git commit -m 'your commit message here'` to commit the modifications and `git push` to push your modifications to GitHub.
9. Create a Pull Request (PR) by going to your fork, <https://github.com/Your_Github_Handle/wrongsecrets> and click on the "New Pull Request" button. The target branch should typically be the Master branch. When submitting a PR, be sure to follow the checklist that is provided in the PR template. The checklist itself will be filled out by the reviewer.
10. Your PR will be reviewed and comments may be given. In order to process a comment, simply make modifications to the same branch as before and push them to your repository. GitHub will automatically detect these changes and add them to your existing PR.
11. When starting on a new PR in the future, make sure to always keep your local repo up to date:

    ```bash
    git fetch upstream
    git merge upstream/develop
    ```

    See also the following article for further explanation on "[How to Keep a Downstream git Repository Current with Upstream Repository Changes](https://medium.com/sweetmeat/how-to-keep-a-downstream-git-repository-current-with-upstream-repository-changes-10b76fad6d97 "How to Keep a Downstream git Repository Current with Upstream Repository Changes")".

If at any time you want to work on a different issue, you can simply switch to a different branch, as explained in step 5.

> Tip: Don't try to work on too many issues at once though, as it will be a lot more difficult to merge branches the longer they are open.

## What not to do

Although we greatly appreciate any and all contributions to the project, there are a few things that you should take into consideration:

-   The Wrongsecrets project should not be used as a platform for advertisement for commercial tools, companies or individuals. Write-ups should be written with free and open-source tools in mind and commercial tools are typically not accepted, unless as a reference in the security tools section.
-   Unnecessary self-promotion of tools or blog posts is frowned upon. If you have a relation with on of the URLs or tools you are referencing, please state so in the PR so that we can verify that the reference is in line with the rest of the guide.

Please be sure to take a careful look at our [Code of Conduct](https://github.com/OWASP/wrongsecrets/blob/master/CODE_OF_CONDUCT.md) for all the details.

---
# Beginner guide
## OWASP WrongSecrets

Basically, [*WrongSecrets*](https://owasp.org/www-project-wrongsecrets/) is an application which teaches how to not store secrets by offering challenges to the user, which helps the user to Self-reflect and correct those mistakes.

## Prerequisites

1. **Docker**
   [*Docker*](https://www.docker.com/) is a software platform that allows you to build, test, and deploy applications quickly and in a more efficient manner.


2. **Node.Js**
   [*Node.Js*](https://nodejs.org/en/) is an open-source library and a cross-platform JavaScript **runtime environment** specifically for running web applications outside one's browser.


3. **JDK-19**
   [*JDK*](https://www.oracle.com/java/technologies/javase/jdk19-archive-downloads.html) is a tool used in development and testing programs written in the Java programming language.


3. **IntelliJ IDEA**
   [*IntelliJ IDEA*](https://www.jetbrains.com/idea/download/#section=windows) is an integrated development environment basically an **IDE** written in Java for developing software written in Java, Kotlin, Groovy etc.


4. **GitHub Desktop**
   [*GitHub Desktop*](https://desktop.github.com/) is an application that enables you to interact with GitHub using a **GUI** instead of the command line or a web browser.
   (*Not Mandatory but is recommended for beginners*)

---


## How to get started with the project in IntelliJ IDEA

- ### Step 1: Fork the Project.
  Navigate to the landing page of the repository in your web browser and click on the ***Fork*** button on the repository’s home page.
  A forked copy of that Git repository will be added to your personal GitHub.
  ![](images/fork-project-1.png)

- ### Step 2: Clone the Project.
  A **clone** is a full copy of a repository, including all logging and versions of files.
  To ***clone*** the Project to your local desktop by clicking on the button as shown below.
  ![](images/clone-project-2.png)

- ### Step 3: Open the Project using IntelliJ IDEA
    - ***Open*** the Cloned Project using IntelliJ IDEA by clicking on the button as shown below.
      ![](images/open-project-3.1.png)

    - **Wait** till the Project Loads.
      ![](images/wait-3.2.png)

- ### Step 4: Setup.
    - Open Settings by pressing ***Ctrl+Alt+S***
      ![](images/open-settings-4.1.png)

    - Follow the path ***IDE settings>Language & Frameworks > Lombok*** and then click on ***Lombok.***
      ![](images/lombok-setup-4.2.png)

    - Make sure that the ***Lombok processing*** is enabled.
      ![](images/lombok-processing-4.3.png)

- ### Step 5: Reload the project
    - Open the ***Maven*** Tab
      ![](images/open-maven-5.1.png)

    - Press the ***Reload*** button as shown below and allow the project to Reload.
      ![](images/reload-maven-5.2.png)

- ### Step 6: Running the Project.
    - Open the ***WrongSecretsApplication*** by following the path ***main>java>org.owasp.wrongsecrets>WrongSecretApplication***.
      ![](images/open-application-6.1.png)
    - Press ***Shift+F10*** to run the application, this will open up the ***Run/Debug Configurations Menu.***
      ![](images/run-application-6.2.png)

- ### Step 7: Setting up Configurations.
    -  Select ***Edit configuration templates*** then select ***Application*** section.
       ![](images/edit-config-7.1.png)
    - There under the ***Application*** section click on the button shown below.
      ![](images/modify-options-7.2.png)
    - ***Select*** all the fields that are Selected in the below picture.
      ![](images/select-options-7.3.png)
    - ***Fill*** all the fields as shown below.
      ![](images/fill-fields-7.4.png)
    - Again press ***Shift+F10*** which runs the Application.
      ![](images/run-application-6.2.png)

- ### There you have it, ***WrongSecrets*** running successfully.
    - Here is a *preview* on how does it look after successfully running the Application.
      **Note:** Running the Application doesn't open any kind of ***GUI***, it only initializes the ***local webserver*** that you can open via a ***browser.***
      ![](images/final-output-8.png)

    - Here is the preview of the **web server**,  you can try to find the secrets by means of solving the challenge offered at:
      [**Challenges**](https://github.com/OWASP/wrongsecrets#basic-docker-exercises)
      ![](images/screenshot.png)
      ### Since, now you have a running application, you can try adding [*New challenges.*](https://github.com/OWASP/wrongsecrets#how-to-add-a-challenge)

---
