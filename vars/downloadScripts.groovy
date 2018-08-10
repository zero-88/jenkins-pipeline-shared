#!/usr/bin/env groovy

def call(String folder = '/tmp') {
    sh "curl -o ${folder}/docker-join-network.sh https://gist.githubusercontent.com/zero-88/ca37f7adeaeee13fabc7591aad4ba2f3/raw/4d6d2b1d309cef831aa59b759dad7a7973e1eb9d/docker-join-network.sh"
    sh "curl -o ${folder}/genpwd.sh https://gist.githubusercontent.com/zero-88/ad4b61759f2e260182de5e67b3c47484/raw/42bf913bc60df4eaa1b5e3473f80dc1ceea7ac08/genpwd.sh"
    sh "chmod +x ${folder}/*.sh"
}