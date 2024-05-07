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
                        // Test tasks
                    } catch (Exception e) {
                        echo "Test failed, marking build as unstable"
                        currentBuild.result = 'UNSTABLE'
                        return
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
