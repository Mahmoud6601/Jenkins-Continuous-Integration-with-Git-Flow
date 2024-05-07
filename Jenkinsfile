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
                // Put your test script here.
                echo 'Testing...'
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
                // Put your deployment script here.
                echo 'Deploying...'
            }
        }
    }
}
