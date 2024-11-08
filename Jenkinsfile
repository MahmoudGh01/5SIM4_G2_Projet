pipeline {
    agent any
    stages {
        stage("Cloning") {
            steps {
                echo "======== Cloning with Git ========"
                git url: "git@github.com:Anas-REBAI/5SIM4_G2_Projet.git",
                    branch: "MohamedAmineLarbi-5Sim4-G2",
                    credentialsId: "github"
            }
        }
        stage("Compiling") {
            steps {
                echo "======== Compiling with Maven ========"
                sh "mvn clean compile"
            }
        }
        stage("Testing (JUnit & Mockito)") {
            steps {
                echo "======== Running Unit Tests with Maven ========"
                sh "mvn clean test"
            }
        }
        stage("Packaging") {
            steps {
                echo "======== Packaging with Maven ========"
                sh "mvn clean package"
            }
        }
        stage("SonarQube Scan") {
            steps {
                echo "======== Analyzing with SonarQube ========"
                withSonarQubeEnv(installationName: 'sonarqube-server') {
                    sh 'mvn sonar:sonar'
                }
            }
        }
    stage('Deploy to Nexus') {
              steps {
                  script {
                      nexusArtifactUploader(
                          nexusVersion: 'nexus3',
                          protocol: 'http',
                          nexusUrl: '192.168.33.10:8081',
                          groupId: 'tn.esprit.spring',
                          artifactId: 'gestion-station-ski',
                          version: '1.0.0',
                          repository: 'mohamedaminelarbi',
                          credentialsId: 'NEXUS',
                          artifacts: [
                              [
                                  artifactId: 'gestion-station-ski',
                                  classifier: '',
                                 file: 'target/gestion-station-ski-1.0.0.jar',
                                  type: 'jar'
                              ]
                          ]
                      )
                  }
              }
          }

        stage("Building Docker Image") {
            steps {
                sh "docker build -t mohamedaminelarbi/mohamedaminelarbi_stationski ."
            }
        }
        stage("Pushing to DockerHub") {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
                    sh "docker push mohamedaminelarbi/mohamedaminelarbi_stationski"
                }
            }
        }
        stage("Stopping Containers") {
            steps {
                sh "docker-compose down"
            }
        }
        stage("Running Containers") {
            steps {
                sh "docker-compose up -d"
            }
        }
       stage("Mail Notification") {
                   steps {
                       script {
                           echo "======== Sending Email Notification ========"
                           def sonarQubeUrl = 'http://192.168.33.10:9000/dashboard?id=tn.esprit.spring%3Agestion-station-ski'
                           mail to: 'mohamedamine.larbi@esprit.tn',
                                subject: "Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' Finished",
                                body: """
                                   <html>
                                       <body>
                                           <h2 style="color: ${currentBuild.currentResult == 'SUCCESS' ? 'green' : 'red'};">Build ${currentBuild.currentResult}</h2>
                                           <p><strong>Job Name:</strong> ${env.JOB_NAME}</p>
                                           <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
                                           <p><strong>Status:</strong> ${currentBuild.currentResult}</p>
                                           <p><strong>Build URL:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                                           <p><strong>SonarQube Report:</strong> <a href="${sonarQubeUrl}">${sonarQubeUrl}</a></p>

                                           <table border="1" cellpadding="5" cellspacing="0" style="border-collapse: collapse; width: 100%;">
                                               <thead>
                                                   <tr>
                                                       <th style="text-align: left;">Stage</th>
                                                       <th style="text-align: left;">Status</th>
                                                   </tr>
                                               </thead>
                                               <tbody>
                                                   <tr>
                                                       <td>Cloning</td>
                                                       <td style="color: green;">Success ✔️</td>
                                                   </tr>
                                                   <tr>
                                                       <td>Compiling</td>
                                                       <td style="color: green;">Success ✔️</td>
                                                   </tr>
                                                   <tr>
                                                       <td>Testing (JUnit & Mockito)</td>
                                                       <td style="color: green;">Success ✔️</td>
                                                   </tr>
                                                   <tr>
                                                       <td>Packaging</td>
                                                       <td style="color: green;">Success ✔️</td>
                                                   </tr>
                                                   <tr>
                                                       <td>SonarQube Scan</td>
                                                       <td style="color: green;">Success ✔️</td>
                                                   </tr>
                                                   <tr>
                                                       <td>Deploy to Nexus</td>
                                                       <td style="color: green;">Success ✔️</td>
                                                   </tr>
                                                   <tr>
                                                       <td>Building Docker Image</td>
                                                       <td style="color: green;">Success ✔️</td>
                                                   </tr>
                                                   <tr>
                                                       <td>Pushing to DockerHub</td>
                                                       <td style="color: green;">Success ✔️</td>
                                                   </tr>
                                                   <tr>
                                                       <td>Stopping Containers</td>
                                                       <td style="color: green;">Success ✔️</td>
                                                   </tr>
                                                   <tr>
                                                       <td>Running Containers</td>
                                                       <td style="color: green;">Success ✔️</td>
                                                   </tr>
                                               </tbody>
                                           </table>
                                           <br/>
                                           <p>Best regards,<br/>Jenkins</p>
                                       </body>
                                   </html>
                                """
                       }
                   }
               }
           }
           post {
               success {
                   mail to: 'mohamedamine.larbi@esprit.tn',
                        subject: "Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' Succeeded",
                        body: """
                           <html>
                               <body>
                                   <h2 style="color: green;">Build Succeeded</h2>
                                   <p><strong>Job Name:</strong> ${env.JOB_NAME}</p>
                                   <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
                                   <p><strong>Status:</strong> SUCCESS</p>
                                   <p><strong>Build URL:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                               </body>
                           </html>
                        """
               }
               failure {
                   mail to: 'mohamedamine.larbi@esprit.tn',
                        subject: "Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' Failed",
                        body: """
                           <html>
                               <body>
                                   <h2 style="color: red;">Build Failed</h2>
                                   <p><strong>Job Name:</strong> ${env.JOB_NAME}</p>
                                   <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
                                   <p><strong>Status:</strong> FAILURE</p>
                                   <p><strong>Build URL:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                               </body>
                           </html>
                        """
               }
           }
       }