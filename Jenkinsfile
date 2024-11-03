pipeline {
    agent any

    environment {
        SONARQUBE_ENV = 'SonarQube'
        SONAR_TOKEN = credentials('SonarToken')
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
                    withCredentials([usernamePassword(credentialsId: 'NEXUS', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                    sh 'mvn -X deploy -DskipTests=true -Dnexus.username=$NEXUS_USER -Dnexus.password=$NEXUS_PASS'
                    }
                    echo "Deployment to Nexus completed!"
                }
            }
        }

        stage('Docker Image') {
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
    }

    post {
        always {
            echo 'Pipeline execution completed!'
        }
    }
}