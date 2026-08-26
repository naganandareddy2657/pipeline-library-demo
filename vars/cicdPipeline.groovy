def call(Map config = [:]) {

    def appName     = config.appName
    def environment = config.environment
    def dockerTag   = config.dockerTag
    def deploy      = config.deploy

    def dockerImage = "naganandareddy7/${appName}"

    stage('Build') {
        echo "Building application: ${appName}"

        sh '''
            mvn clean package
        '''
    }

    stage('Docker Build') {
        echo "Building Docker image: ${dockerImage}:${dockerTag}"

        sh """
            docker build \
                -t ${dockerImage}:${dockerTag} .
        """
    }

    stage('Docker Login & Push') {

        echo "Pushing Docker image: ${dockerImage}:${dockerTag}"

        withCredentials([
            usernamePassword(
                credentialsId: 'Docker_credentails',
                usernameVariable: 'DOCKER_USERNAME',
                passwordVariable: 'DOCKER_PASSWORD'
            )
        ]) {

            sh """
                echo "\$DOCKER_PASSWORD" | docker login \
                    -u "\$DOCKER_USERNAME" \
                    --password-stdin

                docker push ${dockerImage}:${dockerTag}
            """
        }
    }

    stage('Deploy') {

        if (deploy) {

            echo "Deploying ${appName}"
            echo "Environment: ${environment}"
            echo "Docker Image: ${dockerImage}:${dockerTag}"

            sh """
                echo "Deployment started"
                echo "Application : ${appName}"
                echo "Environment : ${environment}"
                echo "Image       : ${dockerImage}:${dockerTag}"

                # Add your actual deployment command here
                # ./deploy.sh ${environment} ${dockerImage}:${dockerTag}
            """

        } else {

            echo "DEPLOY=false"
            echo "Skipping deployment"

        }
    }

    stage('CI/CD Completed') {

        echo """
        ==========================================
        CI/CD PIPELINE COMPLETED
        ==========================================
        Application : ${appName}
        Environment : ${environment}
        Docker Tag  : ${dockerTag}
        Deploy      : ${deploy}
        Docker Image: ${dockerImage}:${dockerTag}
        ==========================================
        """
    }
}
