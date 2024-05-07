pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                // Put your build script here.
                echo 'Building...'
            }
        }
        stage('Test') {
            steps {
               script {
                    try {
                        // Test commands
                    } catch (Exception e) {
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                // Put your deployment script here.
                echo 'Deploying...'
            }
        }
    }
}
