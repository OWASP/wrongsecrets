#!/bin/bash

if [ $# -eq 0 ]
  then
    echo "No arguments supplied, please supply a tag eg 'docker-create-and-push.sh <tag=tag, message=\"message\" buildarg=\"buildarg\"> '"
    exit
fi

for ARGUMENT in "$@"
do
   KEY=$(echo $ARGUMENT | cut -f1 -d=)
   KEY_LENGTH=${#KEY}
   VALUE="${ARGUMENT:$KEY_LENGTH+1}"
    export "$KEY"="$VALUE"
done

if test -n "${tag+x}"; then
  echo "tag is set"
else
  echo "Autotagging with new version: ${tag}"
  SCRIPT_PATH=$(dirname $(dirname $(dirname $(readlink -f "$0"))))
  tag=`docker run -it -v ${SCRIPT_PATH}:/data --workdir /data quay.io/pantheon-public/autotag:latest -n`
fi
echo "Version tag: $tag"
echo "tag message: $message"
echo "buildarg supplied: $buildarg"
echo "generating challenge 12-data"
openssl rand -base64 32 | tr -d '\n' > yourkey.txt
# preps for #178:
#echo "Building and publishing to maven central, did you set: a settings.xml file with:"
#echo "<settings>"
#echo "  <servers>"
#echo "    <server>"
#echo "      <id>ossrh</id>"
#echo "      <username>your-jira-id</username>"
#echo "      <password>your-jira-pwd</password>"
#echo "    </server>"
#echo "  </servers>"
#echo "</settings>"

echo "Building and updating pom.xml file so we can use it in our docker"
cd ../.. && mvn clean install && mvn --batch-mode release:update-versions -DdevelopmentVersion=${tag}-SNAPSHOT
git add pomx.ml
git commit -am "Update POM file with new version: ${tag}"
cd .github/scripts && git push
docker buildx create --name mybuilder
docker buildx use mybuilder
echo "creating containers"
docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/addo-example:$tag-no-vault --build-arg "$2" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=without-vault" --push ./../../.
docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/addo-example:$tag-local-vault --build-arg "$2" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=local-vault" --push ./../../.
docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/addo-example:$tag-k8s-vault --build-arg "$2" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=kubernetes-vault" --push ./../../.
docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/wrongsecrets:$tag-no-vault --build-arg "$2" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=without-vault" --push ./../../.
docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/wrongsecrets:$tag-local-vault --build-arg "$2" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=local-vault" --push ./../../.
docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/wrongsecrets:$tag-k8s-vault --build-arg "$2" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=kubernetes-vault" --push ./../../.

#staging (https://arcane-scrubland-42646.herokuapp.com/)
echo "Completed docker upload for X86, now taking care of heroku, do yourself: update Dockerfile.web, then run 'heroku container:login' 'heroku container:push --recursive --arg argBasedVersion=${tag}heroku' and 'heroku container:push --recursive --arg argBasedVersion=${tag}heroku --app=wrongsecrets' and release both (heroku container:release web --app=wrongsecrets)"
#want to release? do heroku container:release web --app=wrongsecrets

echo "tagging version"
#git tag -a $tag -m "$1"
#git push --tags
