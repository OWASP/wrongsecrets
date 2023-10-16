# Revise contributor rankings

## About
This script is used to generate the contributor-list for home.html and for the various markdown pages on Github where we list the contributors: [www-wrongsecrets](https://github.com/OWASP/www-project-wrongsecrets/blob/main/index.md#contributors) [wrongsecrets](https://github.com/OWASP/wrongsecrets/blob/master/README.md#special-thanks--contributors).

## Installation and run

First of all, you need to setup the ```.env``` file which contains the USER_TOKEN variable. You may follow the following steps:
* [Click here](https://github.com/settings/tokens?type=beta) to setup a Fine-grained token
* Create a token. Note that the *USER_TOKEN* requires "repository access" only
* Open the terminal in the same directory as the *main.py* script and type: ```echo USER_TOKEN=github_pat_TOKEN > .env```

This script does not use external libraries so just run it directly, and to do so open the terminal and type:

```sh
python3 main.py
```
