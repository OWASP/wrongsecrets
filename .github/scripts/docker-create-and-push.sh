#!/bin/bash

if [ $# -eq 0 ]
  then
    echo "No arguments supplied, please supply a tag eg 'docker-create-and-push.sh <tag> <message> <buildarg>'"
    exit
fi

echo "tag supplied: $1"
echo "tag message: $2"
echo "buildarg supplied: $3"

echo "tagging version"
git tag -a $1 -m "$2"
git push --tags
docker buildx create --name mybuilder
docker buildx use mybuilder
echo "creating containers"
docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/addo-example:$1-no-vault --build-arg "$3" --build-arg "PORT=8081" --build-arg "argBasedVersion=$1" --build-arg "spring_profile=without-vault" --push ./../../.
docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/addo-example:$1-local-vault --build-arg "$3" --build-arg "PORT=8081" --build-arg "argBasedVersion=$1" --build-arg "spring_profile=local-vault" --push ./../../.
docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/addo-example:$1-k8s-vault --build-arg "$3" --build-arg "PORT=8081" --build-arg "argBasedVersion=$1" --build-arg "spring_profile=kubernetes-vault" --push ./../../.
#staging (https://arcane-scrubland-42646.herokuapp.com/)
echo "Completed docker upload for X86, now taking care of heroku, do yourself: update Dockerfile.web, then run 'heroku container:login' 'heroku container:push --recursive --arg argBasedVersion=$1heroku' and 'heroku container:push --recursive --arg argBasedVersion=$1heroku --app=wrongsecrets' and release both (heroku container:release web --app=wrongsecrets)"
#want to release? do heroku container:release web --app=wrongsecrets
