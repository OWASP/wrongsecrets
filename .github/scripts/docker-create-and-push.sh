#!/bin/bash

if [ $# -eq 0 ]
  then
    echo "No arguments supplied, please supply a tag eg 'docker-create-and-push.sh <tag> <buildarg>'"
    exit
fi

echo "tag supplied: $1"
echo "buildarg supplied: $2"

docker build --build-arg "$2" --build-arg "spring_profile=without-vault" -t jeroenwillemsen/addo-example:$1-no-vault ./../../.
docker push jeroenwillemsen/addo-example:$1-no-vault
docker build --build-arg "$2" --build-arg "spring_profile=local-vault" -t jeroenwillemsen/addo-example:$1-local-vault ./../../.
docker push jeroenwillemsen/addo-example:$1-local-vault
docker build --build-arg "$2" --build-arg "spring_profile=kubernetes-vault" -t jeroenwillemsen/addo-example:$1-k8s-vault ./../../.
docker push jeroenwillemsen/addo-example:$1-k8s-vault