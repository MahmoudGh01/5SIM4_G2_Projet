pipeline {
    agent any

    environment {
        SONARQUBE_ENV = 'SonarQube'
        SONAR_TOKEN = credentials('SonartDevops')
        DOCKER_CREDENTIALS_ID = 'DOCKER'
    }

    stages {

        stage('GIT') {
            steps {
                echo 'Pulling from Git...'
                git branch: 'MahmoudGharbi-5sin4-G2',
                    url: 'https://github.com/Anas-REBAI/5SIM4_G2_Projet.git'
            }
        }

        stage('COMPILING') {
            steps {
                script {
                    // Clean and install dependencies
                    sh 'mvn clean install'
                }
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
                        nexusUrl: "192.168.124.128:8081",
                        groupId: 'tn.esprit.spring',
                        artifactId: 'gestion-station-ski',
                        version: '1.0',
                        repository: "maven-releases",
                        credentialsId: "NEXUS",
                        artifacts: [
                            [
                               artifactId: 'gestion-station-ski',
                               classifier: '',
                               file: 'target/gestion-station-ski-1.0.jar',
                               type: 'jar'
                            ]
                        ]
                    )

                    echo "Deployment to Nexus completed!"
                }
            }
        }

        stage('Docker Build and Push') {
            steps {
                script {
                    // Build Docker image
                    echo 'Building Docker image...'
                    sh 'docker build -t mahmoudgh01/gestion-station-ski:1.0 .'

                    // Login to Docker Hub with a single credential ID
                    withCredentials([usernamePassword(credentialsId: "${DOCKER_CREDENTIALS_ID}", usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        sh 'echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin'
                    }

                    // Push Docker image
                    echo 'Pushing Docker image to Docker Hub...'
                    sh 'docker push mahmoudgh01/gestion-station-ski:1.0'

                    echo 'Docker image successfully pushed to Docker Hub!'
                }
            }
        }

        stage('Docker Compose Up') {
            steps {
                script {
                    echo 'Starting services with Docker Compose...'
                    sh 'docker-compose up -d'
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline execution completed!'
        }
    }
}
