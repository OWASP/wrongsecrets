# Revise contributor rankings

## About
This script is used to generate the contributor-list for home.html and for the various markdown pages on Github where we list the contributors: [www-wrongsecrets](https://github.com/OWASP/www-project-wrongsecrets/blob/main/index.md#contributors) [wrongsecrets](https://github.com/OWASP/wrongsecrets/blob/master/README.md#special-thanks--contributors).

## Installation and run

1. Make sure you get a fine grained access token from github to read the repositories (https://github.com/settings/tokens?type=beta):
- go to https://github.com/settings/tokens?type=beta
- create a token and make sure it has "repository access" only
- store the token in a safe secrets manager and export it before you run it (`export USER_TOKEN=github_pat<redacted>`).

2. When on macos, install the requests library if you are using python 3.12 or higher:

```sh
brew install pipenv
pipenv install requests
```

3. Now run the script;

```sh
pipenv shell
python3 main.py
```
