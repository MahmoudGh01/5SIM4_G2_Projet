pipeline {
    agent any
    environment {
        SONARQUBE_ENV = 'SonarQube'
        SONAR_TOKEN = credentials('SonarToken')
    }
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
        stage('NEXUS') {
                    steps {
                        script {
                            echo "Deploying to Nexus..."

                            nexusArtifactUploader(
                                nexusVersion: 'nexus3',
                                protocol: 'http',
                                nexusUrl: "192.168.50.4:8081", // Updated Nexus URL based on previous info
                                groupId: 'tn.esprit.spring',
                                artifactId: 'gestion-station-ski',
                                version: '1.0',
                                repository: "maven-releases", // Based on previous Nexus repo
                                credentialsId: "NEXUS", // Using your stored Nexus credentials
                                artifacts: [
                                    [
                                        artifactId: 'gestion-station-ski',
                                        classifier: '',
                                        file: '/var/lib/jenkins/workspace/AminePipeline/target/gestion-station-ski-1.0.jar', // Relative path from workspace
                                        type: 'jar'
                                    ]
                                ]
                            )

                            echo "Deployment to Nexus completed!"
                        }
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
