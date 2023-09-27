#!/bin/bash

################################################################################
# Help                                                                         #
################################################################################
Help() {
    # Display Help
    echo "A versatile script to create a docker image for testing. Call this script with no arguments to simply create a local image that you can use to test your changes. For more complex use see the below help section"
    echo
    echo "Syntax: docker-create.sh [-h (help)|-t (test)|-p (publish)|-e (herokud)|-f (herokup)|-o (okteto)|-n (notag)| -r (Render)|tag={tag}|message={message}|buildarg={buildarg}|springProfile={springProfile}]"
    echo "options: (All optional)"
    echo "tag=             Write a custom tag that will be added to the container when it is build locally."
    echo "message=         Write a message used for the actual tag-message in git"
    echo "buildarg=        Write a build argument here that will be used as the answer to challenge 4."
    echo "springProfile=   Specify a certain build. Options: without-vault, local-vault, kubernetes-vault"
    echo
}

################################################################################
# Heroku helpers                                                               #
################################################################################

break_on_tag(){
  if test -n "${tag+x}"; then
          echo "tag is set"
      else
        echo "tag ${tag} was not set properly, aborting"
        exit
      fi
}

Okteto_redeploy(){
  break_on_tag
  echo "Rebuilding the Okteto environment: https://wrongsecrets-commjoen.cloud.okteto.net/"
  echo "Check if all required binaries are installed"
  source ../../scripts/check-available-commands.sh
  checkCommandsAvailable okteto
  echo "validating okteto k8 deployment to contain the right container with tag "${tag}" (should be part of '$(cat ../../okteto/k8s/secret-challenge-deployment.yml | grep image)')"
  if [[ "$(cat ../../okteto/k8s/secret-challenge-deployment.yml | grep image)" != *"${tag}"* ]]; then
    echo "tag ${tag} in  ../../okteto/k8s/secret-challenge-deployment.yml not properly set, aborting"
    exit
  fi
  cd ../../okteto
  okteto destroy
  okteto deploy
}

heroku_check_container() {
    break_on_tag
    echo "validating dockerfile to contain tag "${tag}" (should be part of '$(head -n 1 ../../Dockerfile.web)')"
    if [[ "$(head -n 1 ../../Dockerfile.web)" != *"${tag}"* ]]; then
      echo "tag ${tag} in dockerfile FROM was not set properly, aborting"
      exit
    fi
    echo "Check if all required binaries are installed"
    source ../../scripts/check-available-commands.sh
    checkCommandsAvailable heroku
}

Heroku_publish_demo() {
    echo "preparing heroku deployment to demo"
    heroku_check_container
    heroku container:login
    echo "heroku deployment to demo"
    cd ../..
    heroku container:push --recursive --arg argBasedVersion=${tag}heroku --app arcane-scrubland-42646
    heroku container:release web --app arcane-scrubland-42646
    heroku container:push --recursive --arg argBasedVersion=${tag}heroku,CTF_ENABLED=true,HINTS_ENABLED=false --app wrongsecrets-ctf
    heroku container:release web --app wrongsecrets-ctf
    exit
}

Heroku_publish_prod(){
    echo "preparing heroku deployment to prod"
    heroku_check_container
    heroku container:login
    echo "heroku deployment to prod"
    cd ../..
    heroku container:push --recursive --arg argBasedVersion=${tag}heroku,CANARY_URLS=http://canarytokens.com/feedback/images/traffic/tgy3epux7jm59n0ejb4xv4zg3/submit.aspx,http://canarytokens.com/traffic/cjldn0fsgkz97ufsr92qelimv/post.jsp --app=wrongsecrets
    heroku container:release web --app=wrongsecrets
    exit
}

render_publish(){
    echo "this depends on whether env var RENDER_HOOK is set, it curls the hook"
    curl $RENDER_HOOK
    exit
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
while getopts ":htpergon*" option; do
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
    e) # Helper
        script_mode="heroku_d"
        ;;
    f) # Helper
        script_mode="heroku_p"
        ;;
    r) #Helper
        script_mode="render"
        ;;
    o) #okteto
        script_mode="okteto"
        ;;
    n) #notags
        disable_tagging_in_git="true"
        ;;
    \?|\*) # Invalid option
        echo "Error: Invalid option"
        echo
        Help
        exit
        ;;
    esac
done

# Check all arguments added to the command
################################################
for ARGUMENT in "$@";
do
    if [[ $ARGUMENT != "-h" && $ARGUMENT != "-t" && $ARGUMENT != "-p" && $ARGUMENT != "-e" && $ARGUMENT != "-f" && $ARGUMENT != "-g" && $ARGUMENT != "-o" ]]
    then
        KEY=$(echo "$ARGUMENT" | cut -f1 -d=)
        KEY_LENGTH=${#KEY}
        VALUE="${ARGUMENT:$KEY_LENGTH+1}"
        export "$KEY"="$VALUE"
    fi
done

if test -n "${tag+x}"; then
    echo "tag is set"
else
    SCRIPT_PATH=$(dirname $(dirname $(dirname $(readlink -f "$0"))))
    tag="local-test"
    echo "Setting default tag: ${tag}"
fi

if test -n "${message+x}"; then
    echo "message is set"
else
    SCRIPT_PATH=$(dirname $(dirname $(dirname $(readlink -f "$0"))))
    message="local testcontainer build"
    echo "Setting default message: ${message}"
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

if test -n "${disable_tagging_in_git+x}"; then
  echo "tagging is disabled"
else
  disable_tagging_in_git="false"
fi

if [[ $script_mode == "heroku_d" ]] ; then
  Heroku_publish_demo
elif [[ $script_mode == "heroku_p" ]]; then
  Heroku_publish_prod
elif [[ $script_mode == "render" ]]; then
  render_publish
elif [[ $script_mode == "okteto" ]]; then
  Okteto_redeploy
fi


local_extra_info() {
    if [[ $script_mode == "local" ]] ; then
        echo ""
        echo "⚠️⚠️ This script is running in local mode, with no arguments this script will build your current code and package into a docker container for easy local testing"
        echo "If the container gets built correctly you can run the container with the command: docker run -p 8080:8080 jeroenwillemsen/wrongsecrets:local-test, if there are errors the script should tell you what to do ⚠️⚠️"
        echo ""
    fi
}

check_required_install() {
    echo "Check if all required binaries are installed"
    source ../../scripts/check-available-commands.sh
    checkCommandsAvailable java docker mvn git curl
    echo "Checking if gsed or sed is installed"
    if [ -x "$(command -v "gsed")" ] ; then
        echo "gsed is installed"
        findAndReplace="gsed"
    elif [ -x "$(command -v "sed")" ] ; then
        echo "sed is installed"
        findAndReplace="sed"
    else
        echo "Error: sed or gsed is not installed, please install one of these"
        exit 1
    fi
}

check_os() {
    echo "Checking for compatible operating system"
    unameOut="$(uname -s)"
    case "${unameOut}" in
    Darwin*)
        echo "OSX detected 🍎"
        ;;
    Linux*)
        echo "Linux detected 🐧"
        ;;
    MINGW64*|CYGWIN)
        echo "Windows detected 🗔"
        ;;
    *)
        echo "🛑🛑 Unknown operating system, this script has only been tests on Windows, Mac OS and Ubuntu. Please be aware there may be some issues 🛑🛑"
        ;;
    esac
}

check_correct_launch_location() {
    if [[ "$(pwd)" != *"scripts"* ]]; then
        echo "🛑🛑 Please run the script from the scripts folder as it causes issues with the steps that cannot be expected 🛑🛑"
        echo "🛑🛑 You are currently running it from $(pwd) 🛑🛑"
        exit 1
    fi
}

generate_test_data() {
    echo "Generating challenge 12-data"
    openssl rand -base64 32 | tr -d '\n' > yourkey.txt
    echo "Generating challenge 16-data"
    SECENDKEYPART1=$(openssl rand -base64 5 | tr -d '\n')
    SECENDKEYPART2=$(openssl rand -base64 3 | tr -d '\n')
    SECENDKEYPART3=$(openssl rand -base64 2 | tr -d '\n')
    SECENDKEYPART4=$(openssl rand -base64 3 | tr -d '\n')
    echo -n "${SECENDKEYPART1}9${SECENDKEYPART2}6${SECENDKEYPART3}2${SECENDKEYPART4}7" > secondkey.txt
    printf "function secret() { \n var password = \"$SECENDKEYPART1\" + 9 + \"$SECENDKEYPART2\" + 6 + \"$SECENDKEYPART3\" + 2 + \"$SECENDKEYPART4\" + 7;\n return password;\n }\n" > ../../js/index.js
    echo "Generating challenge 17"
    rm thirdkey.txt
    openssl rand -base64 32 | tr -d '\n' > thirdkey.txt
    answer=$(<thirdkey.txt)
    answerRegexSafe="$(printf '%s' "$answer" | $findAndReplace -e 's/[]\/$*.^|[]/\\&/g' | $findAndReplace ':a;N;$!ba;s,\n,\\n,g')"
    cp ../../src/main/resources/.bash_history .
    $findAndReplace -i "s/Placeholder Password, find the real one in the history of the container/$answerRegexSafe/g" .bash_history
}

build_update_pom() {
    echo "Building new license overview"
    cd ../.. && ./mvnw license:add-third-party -Dlicense.excludedScopes=test
    cd .github/scripts
    echo "preprocessing third party file"
    sed '/^$/d' ../../target/generated-sources/license/THIRD-PARTY.txt  > temp1a.txt
    sed '/^Lists/ s/./                        &/' temp1a.txt  > temp1.txt
    sed 's/^     /                        <li>/' temp1.txt > temp2.txt
    sed 's/$/<\/li>/' temp2.txt > temp3.txt
    echo "refreshing licenses into the file"
    sed -n '1,/MARKER-start/p;/MARKER-end/,$p' ../../src/main/resources/templates/about.html | gsed '/MARKER-end-->/e cat temp3.txt ' > temp4.txt
    mv temp4.txt ../../src/main/resources/templates/about.html
    rm tem*.txt
    echo "Building and updating pom.xml file so we can use it in our docker"
    cd ../.. && ./mvnw clean && ./mvnw --batch-mode release:update-versions -DdevelopmentVersion=${tag}-SNAPSHOT && ./mvnw install -DskipTests
    cd .github/scripts
    docker buildx create --name mybuilder
    docker buildx use mybuilder
}

create_containers() {
    echo "Creating containers"
    if [[ "$script_mode" == "publish" ]]; then
        docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/addo-example:$tag-no-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=without-vault" --push ./../../.
        docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/addo-example:latest-no-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=without-vault" --push ./../../.
        docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/addo-example:$tag-local-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=local-vault" --push ./../../.
        docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/addo-example:latest-local-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=local-vault" --push ./../../.
        docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/addo-example:$tag-k8s-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=kubernetes-vault" --push ./../../.
        docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/addo-example:latest-k8s-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=kubernetes-vault" --push ./../../.
        docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/wrongsecrets:$tag-no-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=without-vault" --push ./../../.
        docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/wrongsecrets:latest-no-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=without-vault" --push ./../../.
        docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/wrongsecrets:$tag-local-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=local-vault" --push ./../../.
        docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/wrongsecrets:latest-local-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=local-vault" --push ./../../.
        docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/wrongsecrets:$tag-k8s-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=kubernetes-vault" --push ./../../.
        docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/wrongsecrets:latest-k8s-vault --build-arg "$buildarg" --build-arg "PORT=8081" --build-arg "argBasedVersion=$tag" --build-arg "spring_profile=kubernetes-vault" --push ./../../.
        cd ../..
        docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/wrongsecrets-desktop:$tag -f Dockerfile_webdesktop --push .
        docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/wrongsecrets-desktop:latest -f Dockerfile_webdesktop --push .
        docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/wrongsecrets-desktop-k8s:$tag -f Dockerfile_webdesktopk8s --push .
        docker buildx build --platform linux/amd64,linux/arm64 -t jeroenwillemsen/wrongsecrets-desktop-k8s:latest -f Dockerfile_webdesktopk8s --push .
        cd .github/scripts
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
    git restore ../../src/main/resources/.bash_history
    # rm .bash_history
}

commit_and_tag() {
    if [[ "$script_mode" == "publish" ]]; then
        echo "committing changes and new pom file with version ${tag}"
        git commit -am "Update POM file with new version: ${tag}"
        git push
        if [[ "$disable_tagging_in_git" == "true" ]]; then
          echo "Skip git tagging"
        else
          echo "tagging version with tag '${tag}' and message '${message}'"
          git tag -a $tag -m "${message}"
          git push --tags
        fi
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
        echo "Completed docker upload for X86, now taking care of heroku, do yourself: update Dockerfile.web, then run 'heroku container:login'"
        echo "then for the test container: 'heroku container:push --recursive --arg argBasedVersion=${tag}heroku --app arcane-scrubland-42646' and 'heroku container:release web --app arcane-scrubland-42646'"
        echo "then for the prd container:'heroku container:push --recursive --arg argBasedVersion=${tag}heroku --arg CANARY_URLS=http://canarytokens.com/feedback/images/traffic/tgy3epux7jm59n0ejb4xv4zg3/submit.aspx,http://canarytokens.com/traffic/cjldn0fsgkz97ufsr92qelimv/post.jsp --app=wrongsecrets' and release 'heroku container:release web --app=wrongsecrets'"
        #want to release? do heroku container:release web --app=wrongsecrets
    fi
}

test() {
    source ../../scripts/assert.sh
    if [[ "$script_mode" == "test" ]]; then
        echo "Running the tests"
        echo "Starting the docker container"
        docker run -d -p 8080:8080 jeroenwillemsen/wrongsecrets:local-test
        until $(curl --output /dev/null --silent --head --fail http://localhost:8080); do
            printf '.'
            sleep 5
        done
        response=$(curl localhost:8080)
        assert_contain "$response" "Wondering what a secret is?"
        if [ "$?" == 0 ]; then
            log_success "The container test completed successfully"
        else
            log_failure "The container test has failed, this means that when we built your changes and ran a basic sanity test on the homepage it failed. Please build the container locally and double check the container is running correctly."
        fi
        echo "testing curl for webjar caching"
        curl -I  'http://localhost:8080/webjars/bootstrap/5.2.3/css/bootstrap.min.css'
        echo "Testing complete"
    else
        return
    fi
}

local_extra_info
check_correct_launch_location
check_os
check_required_install
generate_test_data
build_update_pom
create_containers
restore_temp_change
commit_and_tag
echo_next_steps
test
