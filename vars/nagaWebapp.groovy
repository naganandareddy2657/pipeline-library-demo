def call() {

    pipeline {

        agent any

        stages {

            stage('Checkout') {
                steps {
                    echo 'Checking out application code...'
                    checkout scm
                }
            }

            stage('Build') {
                steps {
                    echo 'Building application...'
                    sh 'mvn clean package'
                }
            }
        }

        post {
            success {
                echo 'CI/CD Pipeline completed successfully'
            }

            failure {
                echo 'CI/CD Pipeline failed'
            }
        }
    }
}
