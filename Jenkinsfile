pipeline {
    agent any

    stages {
        stage('Checkout GIT') {
            steps {
                echo 'Pulling from Git...'
                git branch: 'AnasRebai_G2_StationSKI',
                    url: 'https://github.com/Anas-REBAI/5SIM4_G2_Projet.git'
            }
        }

        stage('Compile') {
            steps {
                script {
                    // Clean and install dependencies
                    sh 'mvn clean install -DskipTests'

                    // Optionally, you can run tests and package the application
                    // Uncomment these lines if you want to include them
                    // sh 'mvn test'
                    // sh 'mvn package'
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
