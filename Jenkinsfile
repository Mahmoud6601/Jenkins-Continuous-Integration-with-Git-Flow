pipeline {
    agent {label 'master'}     
     stages {            
        stage('A1') { 
            agent {label 'Node1'} 
            steps {
                sh 'binA'
            }
        }
        stage('A2') {
            agent {label 'Node1'}
            steps {
                sh 'binB' // If this bin fails, all following stages are skipped
            }
        }
// ...        
        stage('C3'){
            agent {label 'Node3'}
            steps {
                sh 'binC'
            }
        }
    }
}
