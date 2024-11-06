pipeline {
    agent any

    environment {
        SONARQUBE_ENV = 'SonarQube'
        SONAR_TOKEN = credentials('SonarToken')
    }

    stages {

        stage('GIT') {
            steps {
                echo 'Pulling from Git...'
                git branch: 'AchrefChaabani-5SIM4-G2',
                    url: 'https://github.com/Anas-REBAI/5SIM4_G2_Projet.git'
            }
        }

        stage('COMPILING') {
            steps {
                script {
                    // Clean and install dependencies
                    sh 'mvn clean install'

                    // Uncomment these lines if you want to run tests and package the application
                    // sh 'mvn test'
                    // sh 'mvn package'
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
         stage('Building image') {
                    steps {
                        script {
                            echo 'Building Docker image...'
                            sh 'docker build -t chaabaniachref/gestion-station-ski:1.0 .'

                              // Login to Docker Hub with a single credential ID
                              withCredentials([usernamePassword(credentialsId: "${DOCKER_CREDENTIALS_ID}", usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                                sh 'echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin'
                              }
                        // Push Docker image
                        echo 'Pushing Docker image to Docker Hub...'
                        sh 'docker push chaabaniachref/gestion-station-ski:1.0'

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
                                file: '/var/lib/jenkins/workspace/Achref pipeline/target/gestion-station-ski-1.0.jar', // Relative path from workspace
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
            echo 'Pipeline execution completed!'
        }
    }
}