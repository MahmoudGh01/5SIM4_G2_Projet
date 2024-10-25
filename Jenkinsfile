pipeline {
    agent any
    stages {
        stage("Clone Repository") {
            steps {
                git url: 'https://github.com/Anas-REBAI/5SIM4_G2_Projet.git', branch: 'MahmoudGharbi-5sin4-G2'
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
                            -Dsonar.password=Mahmoud2001#\
                            -Dsonar.coverage.jacoco.xmlReportPaths=/target/site/jacoco/jacoco.xml
                        """


            }
        }
        stage("Test") {
                    steps {
                        sh 'mvn test'
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