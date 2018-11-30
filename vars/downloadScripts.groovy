#!/usr/bin/env groovy

def call(String folder = '/tmp') {
    sh "curl -o ${folder}/docker-join-network.sh https://raw.githubusercontent.com/zero-88/devops-utils/master/docker/docker-join-network.sh"
    sh "curl -o ${folder}/genpwd.sh https://raw.githubusercontent.com/zero-88/devops-utils/master/genpwd.sh"
    sh "chmod +x ${folder}/*.sh"
}