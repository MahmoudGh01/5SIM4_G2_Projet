pipeline {
    agent any

    environment {
        SONARQUBE_ENV = 'SonarQube'
        SONAR_TOKEN = credentials('SonarToken')
        DOCKER_CREDENTIALS_ID = 'DOCKER'

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
                    sh 'docker build -t chaabaniachref/AchrefChaabani-gestion-station-ski:1.0 .'
                }
            }
//             post {
//                 success {
//                     mail to: 'chaabaniachref212@gmail.com',
//                         subject: "Build Backend - Success",
//                         body: "The Docker image was built successfully."
//                 }
//                 failure {
//                     mail to: 'chaabaniachref212@gmail.com',
//                         subject: "Build Backend - Failure",
//                         body: "Building the Docker image failed."
//                 }
//             }
        }
         stage('Verify Image') {
             steps {
                 script {
                     sh 'docker images | grep chaabaniachref/gestion-station-ski'
                 }
             }
         }
//           stage('Push Image to DockerHub') {
//             steps {
//                 script {
//                     echo 'Logging into Docker Hub...'
//                     sh 'docker login -u chaabaniachref -p $DOCKER_HUB_CREDENTIALS_PSW'
//
//                     echo 'Tagging Docker image...'
//                     sh 'docker tag gestion-station-ski:1.0 chaabaniachref/gestion-station-ski:1.0'
//
//                     echo 'Pushing Docker image to Docker Hub...'
//                     sh 'docker push chaabaniachref/gestion-station-ski:1.0'
//                 }
//             }
//          }
        stage('Push Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'DOCKER_HUB_CREDENTIALS', usernameVariable: 'DOCKER_HUB_USER', passwordVariable: 'DOCKER_HUB_PASS')]) {
                        sh 'docker login -u $DOCKER_HUB_USER -p $DOCKER_HUB_PASS'
                        sh 'docker push chaabaniachref/AchrefChaabani-gestion-station-ski:1.0'
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