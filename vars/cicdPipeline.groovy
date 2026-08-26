def call() {

    pipeline {

        agent any

        stages {

            stage('Build') {
                steps {
                    echo "Application: ${params.APP_NAME}"
                    echo "Environment: ${params.ENVIRONMENT}"

                    sh 'mvn clean package'
                }
            }

            stage('Docker Build') {
                steps {
                    sh """
                        docker build \
                        -t myrepo/${params.APP_NAME}:${params.DOCKER_TAG} .
                    """
                }
            }

            stage('Docker Push') {
                steps {
                    sh """
                        docker push \
                        myrepo/${params.APP_NAME}:${params.DOCKER_TAG}
                    """
                }
            }

            stage('Deploy') {
                when {
                    expression {
                        return params.DEPLOY
                    }
                }

                steps {
                    echo "Deploying ${params.APP_NAME}"
                    echo "Environment: ${params.ENVIRONMENT}"

                    sh "./deploy.sh ${params.ENVIRONMENT}"
                }
            }
        }
    }
}
