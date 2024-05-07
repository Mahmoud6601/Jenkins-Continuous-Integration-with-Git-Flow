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
                        // Test tasks
                    } catch (Exception e) {
                        echo "Test failed, marking build as unstable"
                        currentBuesult = 'UNSTABLE'
                        return
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
