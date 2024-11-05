pipeline {
    agent any
    stages {
        stage("cloning") {
            steps {
                echo "========cloning with git========"
                git url: "git@github.com:Anas-REBAI/5SIM4_G2_Projet.git",
                    branch: "MohamedAmineLarbi-5Sim4-G2",
                    credentialsId:"github"
            }
        }
        stage("compiling") {
            steps {
                echo "========compiling with maven========"
                sh "mvn clean compile"
            }
        }
        stage("Testing") {
            steps {
                echo "========Testing with maven========"
                sh "mvn clean test"
            }
        }
        stage("Packaging") {
            steps {
                echo "========Packaging with maven========"
                sh "mvn clean package"
            }
        }
        stage("Scan"){
            steps{
                echo "========Analyzing with Sonarqube========"
                withSonarQubeEnv(installationName: 'sonarqube-server'){
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage("Deploying nexus") {
            steps {
                echo "========Deploying to Nexus========"
                sh 'mvn clean deploy -DskipTests'
            }
        }
        stage("Building image"){
            steps{
                sh "docker build -t mohamedaminelarbi/mohamedaminelarbi_stationski . "
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
        stage("Stoping containers"){
            steps{
                sh "docker-compose down"
            }
        }
        stage("Running containers"){
            steps{
                sh "docker-compose up -d"
            }
        }
    }
    post {
        always {
            echo "========always========"
        }
        success {
            echo "========pipeline executed successfully========"
        }
        failure {
            echo "========pipeline execution failed========"
        }
    }
}
