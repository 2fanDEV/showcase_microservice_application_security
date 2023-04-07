#!/bin/bash
# https://stackoverflow.com/questions/5367068/clear-a-terminal-screen-for-real
# https://stackoverflow.com/questions/5947742/how-to-change-the-output-color-of-echo-in-linux
# https://unix.stackexchange.com/questions/22726/how-to-conditionally-do-something-if-a-command-succeeded-or-failed

GREEN='\033[0;32m'
RED='\033[0;31m'
CYAN='\033[0;36m'
NO_COLOR='\033[0m'

#/bin/bash ./create_jars.sh

if /bin/bash './create_jars.sh'; then
    printf "${GREEN}ALL JARS SUCCESSFULLY CREATED ${NO_COLOR}"
else
    printf "${RED}JAR CREATION FAILED ${NO_COLOR}"
    exit 1
fi
sleep 5
printf "\033c"


if /bin/bash docker-compose up; then
    printf "${GREEN} ALL DOCKER CONTAINERS STARTED SUCCESSFULLY ${NO_COLOR}"
else
    printf "${RED} DOCKER CONTAINER CREATION FAILED ${NO_COLOR}"
fi

