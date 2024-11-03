pipeline {
    agent any

    environment {
        SONARQUBE_ENV = 'SonarQube'
        SONAR_TOKEN = credentials('SonarToken')
        DOCKER_HUB_CREDENTIALS = credentials('DockerHubCredentials')
        NEXUS_CREDENTIALS = credentials('NEXUS')
        NEXUS_URL = credentials('NEXUS_URL')
    }

    stages {

        stage('Check GIT') {
            agent { label 'master' }
            steps {
                echo 'Pulling from Git repository...'
                git branch: 'AnasRebai_G2_StationSKI',
                    url: 'https://github.com/Anas-REBAI/5SIM4_G2_Projet.git'
            }
        }

        stage('Clean Build && Unit Tests') {
            agent { label 'master' }
            steps {
                script {
                    echo 'Compiling the project...'
                    // Clean and install dependencies
                    sh 'mvn clean install'
                }
            }
        }

        stage('SONARQUBE Analysis') {
            agent { label 'master' }
            steps {
                script {
                    echo 'Running SonarQube analysis...'
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

        stage('Deploy to NEXUS') {
            agent { label 'agent01' }
            steps {
                script {
                    echo "Deploying artifact to Nexus..."

                    nexusArtifactUploader(
                        nexusVersion: 'nexus3',
                        protocol: 'http',
                        nexusUrl: "192.168.50.5:8081",
                        groupId: 'tn.esprit.spring',
                        artifactId: 'gestion-station-ski',
                        version: '1.0',
                        repository: "maven-releases",
                        credentialsId: "NEXUS",
                        artifacts: [
                            [
                                artifactId: 'gestion-station-ski',
                                classifier: '',
                                file: "${WORKSPACE}/target/gestion-station-ski-1.0.jar",
                                type: 'jar'
                            ]
                        ]
                    )

                    echo "Deployment to Nexus completed!"
                }
            }
        }

        stage('Docker Image') {
            agent { label 'agent01' }
            steps {
                script {
                    echo 'Building Docker image with Nexus credentials...'
                    sh """
                        docker build \
                            --build-arg NEXUS_USER=${NEXUS_CREDENTIALS_USR} \
                            --build-arg NEXUS_PASS=${NEXUS_CREDENTIALS_PSW} \
                            --build-arg NEXUS_URL=${NEXUS_URL} \
                            -t gestion-station-ski:1.0 .
                    """
                    echo "Building Docker image completed!"
                }
            }
        }

        stage('DockerHub') {
            agent { label 'agent01' }
            steps {
                script {
                    echo 'Logging into Docker Hub...'
                    sh 'docker login -u $DOCKER_HUB_CREDENTIALS_USR -p $DOCKER_HUB_CREDENTIALS_PSW'

                    echo 'Pushing Docker image to Docker Hub...'
                    sh 'docker push rab3oon/gestion-station-ski-1.0'
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