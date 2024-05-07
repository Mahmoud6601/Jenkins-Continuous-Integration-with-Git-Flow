pipeline {
    agent any
    stages {
        stage('Setup') {
            steps {
                // Setup commands
            }
        }
        stage('Build') {
            steps {
                // Build commands
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
                // Deploy commands
            }
        }
    }
}
