pipeline {
    agent any

    environment {
        SONARQUBE_ENV = 'SonarQube'
        SONAR_TOKEN = credentials('SonarToken')
        DOCKER_CREDENTIALS_ID = 'DOCKER'
        DOCKER_HUB_CREDENTIALS = credentials('DockerHubCredentials')

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
                    sh 'ls -l target/gestion-station-ski-1.0.jar'
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
        stage('Build Docker Image') {
            agent { label 'chaabaniachref' }
            steps {
                script {
                    echo 'Building Docker image with Nexus credentials...'
                    withCredentials([
                        usernamePassword(credentialsId: 'NEXUS', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')
                    ]) {
                        sh """
                            docker build \
                                --build-arg NEXUS_USER=$NEXUS_USER \
                                --build-arg NEXUS_PASS=$NEXUS_PASS \
                                --build-arg NEXUS_URL=$NEXUS_URL \
                                -t gestion-station-ski:1.0 .
                        """
                    }
                    echo "Building Docker image completed!"
                }
            }
        }
//          stage('Building image') {
//              steps {
//                  script {
//                      echo 'Building Docker image...'
//                      sh 'docker build -t chaabaniachref/gestion-station-ski:1.0 .'
//                  }
//              }
//          }
         stage('Verify Image') {
             steps {
                 script {
                     sh 'docker images | grep chaabaniachref/gestion-station-ski'
                 }
             }
         }
        stage('Push Image to DockerHub') {
            agent { label 'agent01' }
            steps {
                script {
                    echo 'Logging into Docker Hub...'
                    sh 'docker login -u $DOCKER_HUB_CREDENTIALS_USR -p $DOCKER_HUB_CREDENTIALS_PSW'

                    echo 'Tagging Docker image...'
                    sh 'docker tag gestion-station-ski:1.0 chaabaniachref/gestion-station-ski:1.0'

                    echo 'Pushing Docker image to Docker Hub...'
                    sh 'docker push chaabaniachref/gestion-station-ski:1.0'
                }
            }
        }
//         stage('Docker Compose Up') {
//             steps {
//                 script {
//                     echo 'Starting services with Docker Compose...'
//                     sh 'docker compose up'
//                 }
//             }
//         }

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