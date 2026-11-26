@Library('gitflow-shared-lib') _

pipeline {
    agent {
        label 'docker-builder'
    }

    options {
        timeout(time: 20, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '10'))
        disableConcurrentBuilds()
        timestamps()
        ansiColor('xterm')
    }

    environment {
        REGISTRY_REPO = "${env.REGISTRY_HOST ?: 'localhost:5000'}/gitflow-payload"
    }

    stages {
        stage('Git Flow Policy & Metadata') {
            steps {
                script {
                    env.SHORT_SHA = sh(script: 'git rev-parse --short=7 HEAD', returnStdout: true).trim()
                    def policy = gitFlowPolicy(
                        branch     : env.BRANCH_NAME ?: 'develop',
                        shortSha   : env.SHORT_SHA,
                        buildNumber: env.BUILD_NUMBER ?: '0'
                    )
                    env.BRANCH_TYPE              = policy.branchType
                    env.TARGET_ENV               = policy.targetEnvironment
                    env.PUBLISH_IMAGE            = policy.publishImage.toString()
                    env.REQUIRES_MANUAL_APPROVAL = policy.requiresManualApproval.toString()
                    env.IMAGE_TAGS               = policy.imageTags.join(',')
                    env.PRIMARY_TAG              = policy.imageTags[0]
                    currentBuild.displayName     = "#${env.BUILD_NUMBER} [${env.BRANCH_TYPE}:${env.PRIMARY_TAG}]"
                }
            }
        }

        stage('Static Analysis & Security Gates') {
            parallel {
                stage('Secret & Artifact Hygiene') {
                    steps {
                        sh '''
                            set -euo pipefail
                            if git ls-files '.env' '*.key' '*.pem' '*.crt' | grep -q .; then
                              echo "ERROR: Tracked secret or key file detected."
                              exit 1
                            fi
                        '''
                    }
                }
                stage('Shell Script Syntax') {
                    steps {
                        sh 'for f in scripts/*.sh; do bash -n "$f"; done'
                    }
                }
                stage('Payload Verification Tests') {
                    steps {
                        sh 'python3 -m unittest discover -s payload/tests -v'
                    }
                }
            }
        }

        stage('Build, Scan & SBOM') {
            steps {
                script {
                    dockerBuildAndScan(
                        imageRepo  : env.REGISTRY_REPO,
                        tags       : env.IMAGE_TAGS.split(',') as List,
                        gitSha     : env.SHORT_SHA,
                        branch     : env.BRANCH_NAME ?: 'develop',
                        buildNumber: env.BUILD_NUMBER ?: '0',
                        push       : env.PUBLISH_IMAGE == 'true'
                    )
                }
            }
        }

        stage('Deploy Staging & Smoke Test') {
            when {
                expression { env.TARGET_ENV == 'staging' }
            }
            steps {
                script {
                    deployAndVerify(
                        environment: 'staging',
                        imageTag   : env.PRIMARY_TAG,
                        expectedSha: env.SHORT_SHA
                    )
                }
            }
        }

        stage('Production Approval Gate') {
            when {
                expression { env.TARGET_ENV == 'production' && env.REQUIRES_MANUAL_APPROVAL == 'true' }
            }
            steps {
                timeout(time: 30, unit: 'MINUTES') {
                    input(
                        message: "Approve production promotion of ${env.PRIMARY_TAG} (${env.BRANCH_NAME})?",
                        ok     : 'Promote to Production'
                    )
                }
            }
        }

        stage('Deploy Production & Smoke Test') {
            when {
                expression { env.TARGET_ENV == 'production' }
            }
            steps {
                script {
                    deployAndVerify(
                        environment: 'production',
                        imageTag   : env.PRIMARY_TAG,
                        expectedSha: env.SHORT_SHA
                    )
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'sbom-*.json', allowEmptyArchive: true
            cleanWs()
        }
        success {
            echo "Pipeline SUCCEEDED for ${env.BRANCH_NAME} (${env.PRIMARY_TAG})."
        }
        failure {
            echo "Pipeline FAILED for ${env.BRANCH_NAME}. Inspect stage logs and rollback status."
        }
    }
}
