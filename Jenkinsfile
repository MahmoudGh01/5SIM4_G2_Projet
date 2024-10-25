pipeline {
    agent any
    environment {
        SONARQUBE_ENV = 'SonarQube'
        SONAR_TOKEN = credentials('SonartDevops')
    }
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
                      script {
                          withSonarQubeEnv("${SONARQUBE_ENV}") {
                              sh """
                                  mvn sonar:sonar \
                                  -Dsonar.login=${SONAR_TOKEN} \
                                  -Dsonar.coverage.jacoco.xmlReportPaths=/target/site/jacoco/jacoco.xml
                              """
                          }
                      }
                  }
              }
        stage("Test") {
                    steps {
                        sh 'mvn test'
                    }

                }
         stage("Deploy") {
                    steps {
                        sh 'mvn deploy'
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