#!/bin/bash
# https://stackoverflow.com/questions/5947742/how-to-change-the-output-color-of-echo-in-linux
# https://unix.stackexchange.com/questions/22726/how-to-conditionally-do-something-if-a-command-succeeded-or-failed
# https://www.baeldung.com/spring-boot-repackage-vs-mvn-package
# https://maven.apache.org/plugins-archives/maven-surefire-plugin-2.12.4/examples/skipping-test.html

GREEN='\033[0;32m'
RED='\033[0;31m'
CYAN='\033[0;36m'
NO_COLOR='\033[0m'

which bash
printf "${CYAN}creating server_discovery_eureka jar \n... \n${NO_COLOR}"

cd server_discovery_eureka
printf "${CYAN} Current Directory: $(pwd)\n${NO_COLOR}"

if /bin/bash ./mvnw clean package spring-boot:repackage -DskipTests; then
    printf "\n\n${GREEN}Successfully created JAR for SERVER_DISCOVERY_EUREKA ${NO_COLOR}\n\n"
    sleep 2
else
    printf "\n\n${RED}Attempt at creating JAR for SERVER_DISCOVERY_EUREKA failed${NO_COLOR}\n\n"
    sleep 5
    exit 1
fi

cd ..
printf "${CYAN} Current Directory: $(pwd)\n${NO_COLOR}"

cd api_gateway
printf "${CYAN} Current Directory: $(pwd)\n${NO_COLOR}"

if /bin/bash ./mvnw clean package spring-boot:repackage -DskipTests; then
    printf "\n\n${GREEN}Successfully created JAR for GATEWAY_SERVICE${NO_COLOR}\n\n"
    sleep 2
else
    printf "\n\n${RED}Attempt at creating JAR for GATEWAY_SERVICE failed${NO_COLOR}\n\n"
    sleep 5
    exit 1
fi

cd ..

cd authorization_service
printf "${CYAN} Current Directory: $(pwd)\n${NO_COLOR}"
if /bin/bash ./mvnw clean package spring-boot:repackage -DskipTests; then
    printf "\n\n${GREEN}Successfully created JAR for AUTHORIZATION_SERVICE${NO_COLOR}\n\n"
    sleep 2
else
    printf "\n\n${RED}Attempt at creating JAR for AUTHORIZATION_SERVICE failed${NO_COLOR}\n\n"
    sleep 5
    exit 1
fi
cd ..

cd token_service
printf "${CYAN} Current Directory: $(pwd)\n${NO_COLOR}"
if /bin/bash ./mvnw clean package spring-boot:repackage -DskipTests; then
    printf "\n\n${GREEN}Successfully created JAR for TOKEN_SERVICE${NO_COLOR}\n\n"
    sleep 2
else
    printf "\n\n${RED}Attempt at creating JAR for TOKEN_SERVICE failed${NO_COLOR}\n\n"
    sleep 5
    exit 1
fi
cd ..

cd userservice
printf "${CYAN} Current Directory: $(pwd)\n${NO_COLOR}"
if /bin/bash ./mvnw clean package spring-boot:repackage -DskipTests; then
    printf "\n\n${GREEN}Successfully created JAR for USER_SERVICE${NO_COLOR}\n\n"
    sleep 2
else
    printf "\n\n${RED}Attempt at creating JAR for USER_SERVICE failed${NO_COLOR}\n\n"
    sleep 5
    exit 1
fi
cd ..

cd blogservice
printf "${CYAN} Current Directory: $(pwd)\n${NO_COLOR}"
if /bin/bash ./mvnw clean package spring-boot:repackage "-Dmaven.test.skip=true"; then
    printf "\n\n${GREEN}Successfully created JAR for BLOG_SERVICE${NO_COLOR}\n\n"
    sleep 2
else
    printf "\n\n${RED}Attempt at creating JAR for BLOG_SERVICE failed${NO_COLOR}\n\n"
    sleep 5
    exit 1
fi
cd ..

cd commentservice
printf "${CYAN} Current Directory: $(pwd)\n${NO_COLOR}"
if /bin/bash ./mvnw clean package spring-boot:repackage -DskipTests; then
    printf "\n\n${GREEN}Successfully created JAR for COMMENT_SERVICE${NO_COLOR}\n\n"
    sleep 2
else
    printf "\n\n${RED}Attempt at creating JAR for COMMENT_SERVICE failed${NO_COLOR}\n\n"
    sleep 5
    exit 1
fi
cd ..

cd statisticsService
printf "${CYAN} Current Directory: $(pwd)\n${NO_COLOR}"
if /bin/bash ./mvnw clean package spring-boot:repackage -DskipTests; then
    printf "\n\n${GREEN}Successfully created JAR for STATISTICS_SERVICE${NO_COLOR}\n\n"
    sleep 2
else
    printf "\n\n${RED}Attempt at creating JAR for STATISTICS_SERVICE failed${NO_COLOR}\n\n"
    sleep 5
    exit 1
fi
cd ..

cd loggingService
printf "${CYAN} Current Directory: $(pwd)\n${NO_COLOR}"
if /bin/bash ./mvnw clean package spring-boot:repackage -DskipTests; then
    printf "\n\n${GREEN}Successfully created JAR for LOGGING_SERVICE${NO_COLOR}\n\n"
    sleep 2
else
    printf "\n\n${RED}Attempt at creating JAR for LOGGING_SERVICE failed${NO_COLOR}\n\n"
    sleep 5
    exit 1
fi
cd ..

