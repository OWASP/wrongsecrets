#!/bin/bash

################################################################################
# Help                                                                         #
################################################################################
Help() {
  # Display Help
  echo "A script to build a wrongsecrets container locally for rapidly testing changes."
  echo
  echo "Syntax: docker-create-test [tag={tag}|buildarg={buildarg}|springProfile={springProfile}]"
  echo "options: (All optional)"
  echo "tag=             Write a custom tag that will be added to the container when it is build locally."
  echo "buildarg=        Write a build argument here that will be used as the answer to challenge 4."
  echo "springProfile=   Specify a certain build. Options: without-vault, local-vault, kubernetes-vault"
  echo
}

################################################################################
################################################################################
# Main program                                                                 #
################################################################################
################################################################################

# Set option to get help menu
#############################
while getopts ":h" option; do
  case $option in
  h) # display Help
    Help
    exit
    ;;
  esac
done

for ARGUMENT in "$@"; do
  KEY=$(echo $ARGUMENT | cut -f1 -d=)
  KEY_LENGTH=${#KEY}
  VALUE="${ARGUMENT:$KEY_LENGTH+1}"
  export "$KEY"="$VALUE"
done

# Check all arguments added to the command
################################################
if test -n "${tag+x}"; then
  echo "tag is set"
else
  SCRIPT_PATH=$(dirname $(dirname $(dirname $(readlink -f "$0"))))
  tag="local-test"
  echo "Setting default tag: ${tag}"
fi

if test -n "${buildarg+x}"; then
  echo "buildarg is set"
else
  buildarg="argBasedPassword='this is on your command line'"
  echo "Setting buildarg to ${buildarg}"
fi

if test -n "${springProfile+x}"; then
  if [[ $springProfile == 'local-vault' ]] || [[ $springProfile == 'without-vault' ]] || [[ $springProfile == 'kubernetes-vault' ]]; then
    echo "Setting springProfile to $springProfile"
  else
    echo "Please specify a springProfile of without-vault, local-vault or kubernetes-vault as a springProfile"
    exit 1
  fi
else
  springProfile="All"
fi

echo "Spring profile: $springProfile"
echo "Version tag: $tag"
echo "buildarg supplied: $buildarg"

echo "Check if all required binaries are installed"
##################################################
source ../../scripts/check-available-commands.sh
checkCommandsAvailable java docker mvn git

echo "Start building assets required for container"
####################################################
echo "Generating challenge 12-data"
openssl rand -base64 32 | tr -d '\n' >yourkey.txt
echo "Generating challenge 16-data"
SECENDKEYPART1=$(openssl rand -base64 5 | tr -d '\n')
SECENDKEYPART2=$(openssl rand -base64 3 | tr -d '\n')
SECENDKEYPART3=$(openssl rand -base64 2 | tr -d '\n')
SECENDKEYPART4=$(openssl rand -base64 3 | tr -d '\n')
echo -n "${SECENDKEYPART1}9${SECENDKEYPART2}6${SECENDKEYPART3}2${SECENDKEYPART4}7" >secondkey.txt
printf "function secret() { \n var password = \"$SECENDKEYPART1\" + 9 + \"$SECENDKEYPART2\" + 6 + \"$SECENDKEYPART3\" + 2 + \"$SECENDKEYPART4\" + 7;\n return password;\n }\n" >../../js/index.js

echo "Building and updating pom.xml file so we can use it in our docker"
########################################################################
cd ../.. && mvn clean && mvn --batch-mode release:update-versions -DdevelopmentVersion=${tag}-SNAPSHOT && mvn install -DskipTests
cd .github/scripts
docker buildx create --name mybuilder
docker buildx use mybuilder

echo "Creating containers"
##########################
if [[ "$springProfile" != "All" ]]; then
  docker buildx build -t jeroenwillemsen/wrongsecrets:$tag-$springProfile --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=$springProfile" --load ./../../.
else
  docker buildx build -t jeroenwillemsen/wrongsecrets:$tag-no-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=without-vault" --load ./../../.
  docker buildx build -t jeroenwillemsen/wrongsecrets:$tag-local-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=local-vault" --load ./../../.
  docker buildx build -t jeroenwillemsen/wrongsecrets:$tag-k8s-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=kubernetes-vault" --load ./../../.
fi

echo "Restoring temporal change"
git restore ../../js/index.js
git restore ../../pom.xml