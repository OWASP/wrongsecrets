#!/bin/bash

if [ $# -eq 0 ]
  then
    echo "No arguments supplied, please supply a tag eg 'docker-create-and-push.sh <tag> <message> <buildarg>'"
    exit
fi

echo "tag supplied: $1"
echo "tag message: $2"
echo "buildarg supplied: $3"

git tag -a $1 -m "$2"
git push --tags
docker build --build-arg "$3" --build-arg "PORT=8081" --build-arg "argBasedVersion=$1" --build-arg "spring_profile=without-vault" -t jeroenwillemsen/addo-example:$1-no-vault ./../../.
docker push jeroenwillemsen/addo-example:$1-no-vault
docker build --build-arg "$3" --build-arg "PORT=8081" --build-arg "argBasedVersion=$1" --build-arg "spring_profile=local-vault" -t jeroenwillemsen/addo-example:$1-local-vault ./../../.
docker push jeroenwillemsen/addo-example:$1-local-vault
docker build --build-arg "$3" --build-arg "PORT=8081" --build-arg "argBasedVersion=$1" --build-arg "spring_profile=kubernetes-vault" -t jeroenwillemsen/addo-example:$1-k8s-vault ./../../.
docker push jeroenwillemsen/addo-example:$1-k8s-vault
#staging (https://arcane-scrubland-42646.herokuapp.com/)
echo "Completed docker upload, now taking care of heroku, do yourself: update Dockerfile.web, then run 'heroku login' 'heroku container:push --recursive --arg argBasedVersion=$1heroku' and 'heroku container:push --recursive --arg argBasedVersion=$1heroku --app=wrongsecrets' and release both (heroku container:release web --app=wrongsecrets)"
#want to release? do heroku container:release web --app=wrongsecrets
