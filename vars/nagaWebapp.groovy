def call(Map config = [:]) {

    def applicationName = config.applicationName
    def dockerImage = config.dockerImage
    def deployEnvironment = config.deployEnvironment

    pipeline {

        agent any

        stages {

            stage('Build') {
                steps {
                    echo "Building ${applicationName}"
                    sh 'mvn clean package'
                }
            }

            stage('Docker Build') {
                steps {
                    sh "docker build -t ${dockerImage}:${BUILD_NUMBER} ."
                }
            }

            stage('Deploy') {
                steps {
                    echo "Deploying ${applicationName} to ${deployEnvironment}"
                }
            }
        }
    }
}
