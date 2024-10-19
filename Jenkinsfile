pipeline {
    agent any
    stages {
        stage("Clone Repository") {
            steps {
                git url: 'https://github.com/Anas-REBAI/5SIM4_G2_Projet.git', branch: 'MohamedAmineLarbi-5Sim4-G2'
            }
        }
        stage("Build") {
            steps {
                sh 'mvn clean compile'  
            }
            
        }
         stage('SONARQUBE') {
            steps {
                 
                        sh """
                            mvn sonar:sonar \
                            -Dsonar.login=admin\
                            -Dsonar.password=Sonar12345678@\
                        """
                    
                
            }
        }
    }
    post {
        always {
            echo "========always========"
        }
        success {
            echo "========pipeline executed successfully ========"
        }
        failure {
            echo "========pipeline execution failed========"
        }
    }
}
