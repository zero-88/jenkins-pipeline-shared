#!/usr/bin/env groovy

def getRepoURL() {
    sh "git config --get remote.origin.url > .git/remote-url"
    return readFile(".git/remote-url").trim()
}
 
def getCommitSha() {
    sh "git rev-parse HEAD > .git/current-commit"
    return readFile(".git/current-commit").trim()
}
 
def updateGithubCommitStatus(build) {
    // workaround https://issues.jenkins-ci.org/browse/JENKINS-38674
    repoUrl = getRepoURL()
    commitSha = getCommitSha()
    echo "Hello ${JOB_NAME}-#${BUILD_NUMBER}::${build.result}"
    step([
        $class: 'GitHubCommitStatusSetter',
        reposSource: [$class: "ManuallyEnteredRepositorySource", url: repoUrl],
        commitShaSource: [$class: "ManuallyEnteredShaSource", sha: commitSha],
        errorHandlers: [[$class: 'ShallowAnyErrorHandler']],
        statusResultSource: [
            $class: 'ConditionalStatusResultSource',
            results: [
                [$class: 'BetterThanOrEqualBuildResult', result: 'SUCCESS', state: 'SUCCESS', message: "The build has succeeded!"],
                [$class: 'BetterThanOrEqualBuildResult', result: 'FAILURE', state: 'ERROR', message: "Oops! Please do it right, dude!"]
            ]
        ]
    ])
}

def call() {
    updateGithubCommitStatus(currentBuild)
}