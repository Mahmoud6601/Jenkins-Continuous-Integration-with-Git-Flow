pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
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
        stage('Deploy') {
            steps {
                echo 'Deploying...'
            }
        }
    }
}
