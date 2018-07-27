#!/usr/bin/env groovy

/**
 * Send notifications based on build status string
 * Git URL Pattern: <protocol>://[<user>[:<password>]@]<hostname>[:<port>][:][/]<path>[#<commit-ish> | #semver:<semver>]
 */
def call(String version = '1.0.0') {
    web_url = GIT_URL.replaceAll(/\.git(#.*)?$/, '').replaceAll(/^git(\+(ssh|https?)\:\/\/git)?\@/, 'https://').replaceAll(/:([^\/])/, '/$1')
    def content = GIT_BRANCH ==~ /^v.+/ && currentBuild.result == 'SUCCESSFUL' ? releaseContent(version) : failureContent(version)
    def prefixSubject = GIT_BRANCH ==~ /^v.+/ ? "[Jenkins] [Release]" : "[Jenkins]"
    if (currentBuild.result == 'FAILURE' || GIT_BRANCH ==~ /^v.+/) {
        emailext (
            recipientProviders: GIT_BRANCH ==~ /^v.+/ ? [] : [[$class: 'CulpritsRecipientProvider'], [$class: 'RequesterRecipientProvider']],
            subject: "${prefixSubject} ${JOB_NAME}-#${BUILD_NUMBER} [${currentBuild.result}]",
            body: "${content}",
            attachLog: true,
            compressLog: true,
            mimeType: 'text/html',
            to: GIT_BRANCH ==~ /^v.+/ ? DEFAULT_RECIPIENTS : ""
        )
    }
}

def releaseContent(String version) {
    def tag_path = web_url =~ /github/ ? 'releases/tag/' : url =~ /gitlab/ ? 'tags' : url =~ /bitbucket/ ? 'src' : 'tags'
    return """
        <p>RELEASE <a href='${web_url}/${tag_path}/${GIT_BRANCH}'>${version}</a></p>
        <ul>
            <li>Job Name: ${JOB_NAME}</li>
            <li>Console: <a href='${BUILD_URL}'>${JOB_NAME} #${BUILD_NUMBER}</a></li>
        </ul>
    """
}

def failureContent(String version) {
    def committerEmail = sh (script: 'git --no-pager show -s --format=\'%ae\'', returnStdout: true).trim()
    def committer = sh (script: 'git --no-pager show -s --format=\'%an\'', returnStdout: true).trim()
    def commit_path = web_url =~ /github|gitlab/ ? 'commit' : url =~ /bitbucket/ ? 'commits' : 'commit'
    def branch_path = web_url =~ /github|gitlab/ ? 'tree' : url =~ /bitbucket/ ? 'src' : 'tree'
    return """
        <p>BUILD ${version}</p>
        <ul>
            <li>Job Name: ${JOB_NAME}</li>
            <li>Console: <a href='${BUILD_URL}'>${JOB_NAME} #[${BUILD_NUMBER}]</a></li>
            <li>Changes:
                <ul>
                    <li>Author: ${committer} <${committerEmail}></li>
                    <li>Branch: <a href='${web_url}/${branch_path}/${GIT_BRANCH}'>${GIT_BRANCH}</a></li>
                    <li>Commit: <a href='${web_url}/${commit_path}/${GIT_COMMIT}'>${GIT_COMMIT}</a></li>
                </ul>
            </li>
        <ul>
    """
}