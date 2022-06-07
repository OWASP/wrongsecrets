#!/bin/bash

################################################################################
# Help                                                                         #
################################################################################
Help() {
    # Display Help
    echo "A script to build a wrongsecrets container locally for rapidly testing changes."
    echo
    echo "Syntax: docker-create.sh [-h|--help|-t|--test|-p|--publish] [tag={tag}|buildarg={buildarg}|springProfile={springProfile}]"
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

# Set options
#############################
# Set option to local if no option provided
script_mode="local"
# Parse provided options
while getopts ":htp*" option; do
    case $option in
    h) # display Help
        Help
        exit
        ;;
    t) # set script to test mode
        script_mode="test"
        ;;
    p) # set script to publish mode
        script_mode="publish"
        ;;
    \?) # Invalid option
        echo "Error: Invalid option"
        echo
        Help
        exit
        ;;
    esac
done

# Check all arguments added to the command
################################################
for ARGUMENT in "$@"; do
    KEY=$(echo $ARGUMENT | cut -f1 -d=)
    KEY_LENGTH=${#KEY}
    VALUE="${ARGUMENT:$KEY_LENGTH+1}"
    export "$KEY"="$VALUE"
done

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

check_required_install() {
    echo "Check if all required binaries are installed"
    ##################################################
    source ../../scripts/check-available-commands.sh
    checkCommandsAvailable java docker mvn git
}

build_update_pom() {
    echo "Building and updating pom.xml file so we can use it in our docker"
    ########################################################################
    cd ../.. && mvn clean && mvn --batch-mode release:update-versions -DdevelopmentVersion=${tag}-SNAPSHOT && mvn install -DskipTests
    cd .github/scripts
    docker buildx create --name mybuilder
    docker buildx use mybuilder
}

create_containers() {
    echo "Creating containers"
    ##########################
    if [[ "$script_mode" == "publish" ]]; then
        docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/addo-example:$tag-no-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=without-vault" --push ./../../.
        docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/addo-example:$tag-local-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=local-vault" --push ./../../.
        docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/addo-example:$tag-k8s-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=kubernetes-vault" --push ./../../.
        docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/wrongsecrets:$tag-no-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=without-vault" --push ./../../.
        docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/wrongsecrets:$tag-local-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=local-vault" --push ./../../.
        docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/wrongsecrets:$tag-k8s-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=kubernetes-vault" --push ./../../.
    elif [[ "$script_mode" == "test" ]]; then
        docker buildx build -t jeroenwillemsen/wrongsecrets:$tag --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=without-vault" --load ./../../.
    else
        if [[ "$springProfile" != "All" ]]; then
            docker buildx build -t jeroenwillemsen/wrongsecrets:$tag-$springProfile --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=$springProfile" --load ./../../.
        else
            docker buildx build -t jeroenwillemsen/wrongsecrets:$tag-no-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=without-vault" --load ./../../.
            docker buildx build -t jeroenwillemsen/wrongsecrets:$tag-local-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=local-vault" --load ./../../.
            docker buildx build -t jeroenwillemsen/wrongsecrets:$tag-k8s-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=kubernetes-vault" --load ./../../.
        fi
    fi
}

restore_temp_change() {
    echo "Restoring temporal change"
    git restore ../../js/index.js
    git restore ../../pom.xml
}

commit_and_tag() {
    if [[ "$script_mode" == "publish" ]]; then
        echo "committing changes and new pom file with version ${tag}"
        git commit -am "Update POM file with new version: ${tag}"
        git push
        echo "tagging version"
        git tag -a $tag -m "${message}"
        git push --tags
    else
        return
    fi
}

echo_next_steps() {
    if [[ "$script_mode" == "publish" ]]; then
        echo "Don't forget to update experiment-bed"
        echo "git checkout experiment-bed && git merge master --no-edit"
        echo "git push"

        #staging (https://arcane-scrubland-42646.herokuapp.com/)
        echo "Completed docker upload for X86, now taking care of heroku, do yourself: update Dockerfile.web, then run 'heroku container:login' 'heroku container:push --recursive --arg argBasedVersion=${tag}heroku' and 'heroku container:push --recursive --arg argBasedVersion=${tag}heroku --arg CANARY_URLS=http://canarytokens.com/feedback/images/traffic/tgy3epux7jm59n0ejb4xv4zg3/submit.aspx,http://canarytokens.com/traffic/cjldn0fsgkz97ufsr92qelimv/post.jsp --app=wrongsecrets' and release both (heroku container:release web --app=wrongsecrets)"
        #want to release? do heroku container:release web --app=wrongsecrets
    else
        return
    fi
}

test() {
    if [[ "$script_mode" == "test" ]]; then
        echo "Running the tests"
        echo "Starting the docker container"
        docker run -d -p 8080:8080 jeroenwillemsen/wrongsecrets:local-test
        sleep 30
        curl localhost:8080/spoil-17
        echo "testing complete"
    else
        return
    fi
}

check_required_install
build_update_pom
create_containers
restore_temp_change
commit_and_tag
echo_next_steps
test
